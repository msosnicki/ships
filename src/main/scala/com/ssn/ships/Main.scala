package com.ssn.ships

import cats.syntax.all._
import cats.effect._
import com.ssn.ships.domain._
import com.ssn.ships.game._
import com.ssn.ships.player._

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    for {
      human <- HumanPlayer.make[IO]
      ai    <- AIPlayer.make[IO]
      _     <- run0[IO](human, ai)
    } yield ExitCode.Success

  def run0[F[_]: Sync: Concurrent](playerA: PlayerBehavior[F], playerB: PlayerBehavior[F]): F[(Player, GameState)] = {
    val interpreter = new GameInterpreter[F](playerA, playerB)
    for {
      (winner, endState) <- interpreter.play
      _                  <- console.printLine(s"Winner is $winner")
      _                  <- console.printLine(s"Winner state:")
      _                  <- console.printLine(GameStatePrint.printPlayerState(endState, winner))
      _                  <- console.printLine(s"Loser state:")
      _                  <- console.printLine(GameStatePrint.printPlayerState(endState, winner.other))
    } yield (winner, endState)
  }

}
