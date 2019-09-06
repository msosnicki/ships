package com.ssn.ships

import cats.data.StateT
import cats.implicits._
import cats.effect.Sync
import com.ssn.ships.domain.{PlayerShips, Point, Ship}

import scala.util.Random

object random {

  def randomPoint[F[_]](implicit S: Sync[F]): F[Point] =
    for {
      x <- randomInt[F](9)
      y <- randomInt[F](9)
    } yield Point(x, y)

  def randomBool[F[_]: Sync]: F[Boolean] = randomInt(1).map(_ == 0)

  def randomShip[F[_]: Sync](size: Int): F[Ship] =
    for {
      a          <- randomPoint
      horizontal <- randomBool
      b <- {
        val validPoints = Point.pointsAtDistance(a, size - 1, horizontal)
        randomInt(validPoints.size - 1).map(validPoints)
      }
      ship <- Ship.create(a, b) match {
        case Some(x) => x.pure[F]
        case None    => randomShip(size)
      }
    } yield ship

  def createRandomShips[F[_]: Sync](sizes: Set[Int]): F[PlayerShips] =
    createShips(sizes)(randomShip[F])

  def createShips[F[_]](sizes: Set[Int])(create: Int => F[Ship])(implicit S: Sync[F]): F[PlayerShips] = {
    def addShip(size: Int): StateT[F, PlayerShips, Unit] = StateT(stateF(_, size))
    def stateF(currentState: PlayerShips, size: Int): F[(PlayerShips, Unit)] =
      for {
        ship <- create(size)
        newShips = PlayerShips(currentState.get + ship)
        state <- if (PlayerShips.validate(newShips)) S.pure(newShips) else stateF(currentState, size).map(_._1)
      } yield (state, ())
    sizes.toList.traverse_(addShip).run(PlayerShips(Set.empty)).map(_._1)
  }

  private def randomInt[F[_]](max: Int)(implicit S: Sync[F]): F[Int] =
    S.delay(Random.nextInt(max + 1))
}
