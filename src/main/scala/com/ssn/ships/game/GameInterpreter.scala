package com.ssn.ships.game

import cats.Monad
import cats.implicits._
import com.ssn.ships.domain._
import com.ssn.ships.player.PlayerBehavior
import fs2._

final class GameInterpreter[F[_]](playerA: PlayerBehavior[F], playerB: PlayerBehavior[F])(
    implicit M: Monad[F],
    C: Stream.Compiler[F, F]
) {

  /**
    * Whole gameplay
    */
  def play: F[(Player, GameState)] =
    for {
      initialState <- pickShipsPhase
      res <- playerA.turns
        .through2(playerB.turns)(gameLoop(initialState))
        .compile
        .toList
    } yield {
      val (endState, _) = res.last
      endState.turnOf -> endState
    }

  /**
    * Picking ships phase
    */
  private def pickShipsPhase: F[GameState] =
    for {
      a <- playerA.initialState
      b <- playerB.initialState
    } yield GameState(PlayerState(a, Set.empty), PlayerState(b, Set.empty), PlayerA)

  /**
    * Main game loop, alternates player turns until game ends
    */
  private def gameLoop(init: GameState): Pipe2[F, Point, Point, (GameState, ShotResult)] = (p1, p2) => {
    def loop(state: GameState): Pull[F, (GameState, ShotResult), Unit] = {
      val (firstPlayer, feedback) = if (state.turnOf == PlayerA) (p1, playerA.feedback _) else (p2, playerB.feedback _)
      playerTurn(state, firstPlayer, feedback).flatMap(loop)
    }
    loop(init).stream
      .takeWhile({ case (_, result) => result != GameWon }, takeFailure = true)
  }

  /**
    * One player turn until he misses or wins
    */
  private def playerTurn(
      state: GameState,
      actions: Actions[F],
      feedback: (Point, ShotResult) => F[Unit]
  ): Pull[F, (GameState, ShotResult), GameState] = {
    def provideFeedback(point: Point, turnResult: TurnResult): Pull[F, INothing, Unit] =
      Pull.eval(turnResult.traverse_ { case (_, sr) => feedback(point, sr) })
    actions.pull.uncons1.flatMap {
      case Some((point, tail)) =>
        Pull
          .pure[F, TurnResult](state.shot(state.turnOf, point))
          .flatTap(provideFeedback(point, _))
          .flatMap {
            case Some((nextState, result)) if nextState.turnOf == state.turnOf =>
              val pullResult = Pull.output1[F, (GameState, ShotResult)](nextState -> result)
              if (result == GameWon) pullResult *> Pull.done.map(_ => nextState)
              else pullResult *> playerTurn(nextState, tail, feedback)
            case Some((nextState, result)) =>
              Pull.output1(nextState -> result).map(_ => nextState)
            case None => turnEnded(state)
          }
      case None => turnEnded(state)
    }
  }

  private def turnEnded(nextState: GameState): Pull[Pure, INothing, GameState] = Pull.done.map(_ => nextState)

}
