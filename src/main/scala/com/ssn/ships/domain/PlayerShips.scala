package com.ssn.ships.domain

import scala.collection.breakOut

case class PlayerShips(get: Set[Ship]) {
  val allPoints: Set[Point] = get.flatMap(_.points)(breakOut)
}

object PlayerShips {
  def validate(ships: PlayerShips): Boolean = {
    val noDuplicatePoints = ships.allPoints.size == ships.get.toList.flatMap(_.points).size
    noDuplicatePoints && {
      val forbiddenLocations = ships.get.flatMap(shipProximity)
      forbiddenLocations.intersect(ships.allPoints).isEmpty
    }
  }

  private def shipProximity(ship: Ship): Set[Point] =
    ship.points.flatMap(proximity) -- ship.points

  private def proximity(point: Point): Set[Point] =
    (for {
      x <- math.max(0, point.x - 1) to math.min(9, point.x + 1)
      y <- math.max(0, point.y - 1) to math.min(9, point.y + 1)
      p = Point(x, y)
    } yield p)(breakOut)
}
