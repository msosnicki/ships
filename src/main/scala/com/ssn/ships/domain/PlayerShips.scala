package com.ssn.ships.domain

import scala.collection.breakOut

case class PlayerShips(get: Set[Ship]) {
  val allPoints: Set[Point] = get.flatMap(_.points)(breakOut)
}

object PlayerShips {
  def validate(ships: PlayerShips): Boolean = {
    val noCollisions = ships.allPoints.size == ships.get.toList.flatMap(_.points).size
    noCollisions && {
      val forbiddenLocations = ships.get.flatMap(areaAroundShip)
      forbiddenLocations.intersect(ships.allPoints).isEmpty
    }
  }

  private def areaAroundShip(ship: Ship): Set[Point] =
    ship.points.flatMap(Point.proximity) -- ship.points

}
