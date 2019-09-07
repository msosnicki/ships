package com.ssn.ships.player

import cats.Applicative
import cats.effect.concurrent.Ref
import cats.effect.{Concurrent, Sync}
import cats.implicits._
import com.ssn.ships.domain._
import com.ssn.ships.game.{Actions, GameStatePrint, ShipSizes}
import com.ssn.ships.{console, random}
import fs2._

trait PlayerBehavior[F[_]] {
  def initialState: F[PlayerShips]
  def turns: Actions[F]

  /**
    * This is the way information about the shot result gets back to the player
    */
  def feedback(point: Point, result: ShotResult): F[Unit]
}

abstract class PlayerWithMemory[F[_]: Sync](mem: Ref[F, Map[Point, ShotResult]]) extends PlayerBehavior[F] {
  def memorize(point: Point, result: ShotResult): F[Unit] =
    mem.update(m => if (m.contains(point)) m else m.updated(point, result))

  def printMemory: F[Unit] =
    mem.get.flatMap(m => {
      val (hits, misses) = m.partition(_._2 != Miss)
      console.printLine(GameStatePrint.printPlayerState(hits.keySet, misses.keySet))
    })

  def newPoint: F[Point] = {
    def loop(retries: Int): F[Point] = mem.get.flatMap(
      m => random.randomPoint[F].flatMap(p => if (m.contains(p) && retries < 100) loop(retries + 1) else p.pure[F])
    )
    loop(0)
  }
}

final class HumanPlayer[F[_]] private (mem: Ref[F, Map[Point, ShotResult]])(implicit S: Sync[F])
    extends PlayerWithMemory[F](mem) {

  //TODO: replace with manually picking ships
  override def initialState: F[PlayerShips] =
    random
      .randomShips(ShipSizes)
      .flatTap(s => console.printLine(GameStatePrint.printShipChoice(s)))

  override def turns: Actions[F] =
    Stream.repeatEval(getPoint)

  override def feedback(point: Point, result: ShotResult): F[Unit] =
    memorize(point, result) *>
      printMemory *>
      console.printLine(s"Last shot result: $result.")

  private val getPoint: F[Point] = console.printLine("Your turn.") *>
    console.readLine.flatMap(
      str =>
        Point.parse(str) match {
          case None    => console.printLine("Point has to be between A1 and J10") *> getPoint
          case Some(p) => S.pure(p)
        }
    )
}

object HumanPlayer {
  def make[F[_]: Sync]: F[HumanPlayer[F]] =
    Ref.of[F, Map[Point, ShotResult]](Map.empty).map(new HumanPlayer[F](_))
}

final class RandomPlayer[F[_]](implicit S: Sync[F]) extends PlayerBehavior[F] {
  override def initialState: F[PlayerShips]                        = random.randomShips(ShipSizes)
  override def turns: Actions[F]                                   = Stream.repeatEval(random.randomPoint[F])
  override def feedback(point: Point, result: ShotResult): F[Unit] = S.unit
}

final class FixedPlayer[F[_]](ships: Set[Ship], shots: List[Point])(implicit A: Applicative[F])
    extends PlayerBehavior[F] {
  override def initialState: F[PlayerShips]                        = A.pure(PlayerShips(ships))
  override def turns: Actions[F]                                   = Stream.emits(shots)
  override def feedback(point: Point, result: ShotResult): F[Unit] = A.unit
}

final class AIPlayer[F[_]: Sync] private (mem: Ref[F, Map[Point, ShotResult]], notSunkHits: Ref[F, Option[Set[Point]]])
    extends PlayerWithMemory[F](mem) {

  override def initialState: F[PlayerShips] =
    random.randomShips(ShipSizes)

  override def turns: Actions[F] = Stream.repeatEval(nextTurn)

  /**
    * Simple algorithm looking if there was unsunk hit, if so,
    * it will shoot at new points at it's proximity
    */
  private val nextTurn: F[Point] = notSunkHits.get.flatMap {
    case None => newPoint
    case Some(v) =>
      mem.get.map(memorized => (v.flatMap(Point.proximity) -- memorized.keySet).headOption).flatMap {
        case Some(p) => p.pure[F]
        case None    => newPoint
      }

  }

  override def feedback(point: Point, result: ShotResult): F[Unit] =
    memorize(point, result) *>
      notSunkHits.get.flatMap(
        current =>
          (current, result) match {
            case (Some(_), Sunk | GameWon) => notSunkHits.set(None)
            case (Some(hits), Hit)         => notSunkHits.set(Some(hits + point))
            case (None, Hit)               => notSunkHits.set(Some(Set(point)))
            case _                         => ().pure[F]
          }
      )
}

object AIPlayer {

  def make[F[_]: Sync: Concurrent]: F[AIPlayer[F]] =
    for {
      mem     <- Ref.of[F, Map[Point, ShotResult]](Map.empty)
      notSunk <- Ref.of[F, Option[Set[Point]]](None)
    } yield new AIPlayer[F](mem, notSunk)
}
