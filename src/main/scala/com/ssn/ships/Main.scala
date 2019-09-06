package com.ssn.ships

import cats.syntax.all._
import cats.effect.{ExitCode, IO, IOApp, Sync}
import com.ssn.ships.game.{GameInterpreter, GameStatePrint, HumanPlayer, RandomPlayer}

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    run0[IO] *> IO(ExitCode.Success)

  private def run0[F[_]: Sync]: F[Unit] = {
    val shipSizes = Set(1, 2, 3, 4)
    for {
      human <- HumanPlayer.make[F](shipSizes)
      random      = new RandomPlayer[F](shipSizes)
      interpreter = new GameInterpreter[F](human, random)
      (winner, endState) <- interpreter.play
      _                  <- console.printLine(s"Winner is $winner")
      _                  <- console.printLine(s"Winner state:")
      _                  <- console.printLine(GameStatePrint.printPlayerState(endState, winner))
      _                  <- console.printLine(s"Loser state:")
      _                  <- console.printLine(GameStatePrint.printPlayerState(endState, winner.other))
    } yield ()
  }

}
