package com.ssn.ships.game

import cats.Applicative
import cats.implicits._
import cats.effect.Sync
import cats.effect.concurrent.Ref
import fs2._
import com.ssn.ships.domain.{Miss, PlayerShips, Point, Ship, ShotResult}
import com.ssn.ships.{console, random}

trait PlayerBehavior[F[_]] {
  def initialState: F[PlayerShips]
  def turns: Actions[F]
  def feedback(point: Point, result: ShotResult): F[Unit]
}

abstract class PlayerWithMemory[F[_]: Sync](mem: Ref[F, Map[Point, ShotResult]]) extends PlayerBehavior[F] {
  override def feedback(point: Point, result: ShotResult): F[Unit] =
    mem.update(_.updated(point, result)) *>
      mem.get.flatMap(m => {
        val (hits, misses) = m.partition(_._2 != Miss)
        console.printLine(GameStatePrint.printPlayerState(hits.keySet, misses.keySet))
      })
}

final class HumanPlayer[F[_]] private (mem: Ref[F, Map[Point, ShotResult]], shipSizes: Set[Int])(implicit S: Sync[F])
    extends PlayerWithMemory[F](mem) {

  //TODO: replace with manually picking ships
  override def initialState: F[PlayerShips] =
    random
      .createRandomShips(shipSizes)
      .flatTap(s => console.printLine(GameStatePrint.printShipChoice(s)))
  override def turns: Actions[F] =
    Stream.repeatEval(getPoint)
  override def feedback(point: Point, result: ShotResult): F[Unit] =
    super.feedback(point, result) *>
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
  def make[F[_]: Sync](shipSizes: Set[Int]): F[HumanPlayer[F]] =
    Ref.of[F, Map[Point, ShotResult]](Map.empty).map(new HumanPlayer[F](_, shipSizes))
}

final class RandomPlayer[F[_]](shipSizes: Set[Int])(implicit S: Sync[F]) extends PlayerBehavior[F] {
  override def initialState: F[PlayerShips]                        = random.createRandomShips(shipSizes)
  override def turns: Actions[F]                                   = Stream.repeatEval(random.randomPoint[F])
  override def feedback(point: Point, result: ShotResult): F[Unit] = S.unit
}

final class FixedPlayer[F[_]](ships: Set[Ship], shots: List[Point])(implicit A: Applicative[F])
    extends PlayerBehavior[F] {
  override def initialState: F[PlayerShips]                        = A.pure(PlayerShips(ships))
  override def turns: Actions[F]                                   = Stream.emits(shots)
  override def feedback(point: Point, result: ShotResult): F[Unit] = A.unit
}

final class AIPlayer[F[_]](shipSizes: Set[Int])(implicit S: Sync[F]) extends PlayerBehavior[F] {
  override def initialState: F[PlayerShips] = random.createRandomShips(shipSizes)

  override def turns: Actions[F] = ???

  override def feedback(point: Point, result: ShotResult): F[Unit] = ???
}
