package com.ssn.ships.domain

import cats.syntax.option._
import scala.collection.breakOut

//TODO: maybe prohibit from manual creation to validate(f.e creation only via Ship.create)
case class Ship(points: Set[Point]) {
  def contains(p: Point): Boolean        = points.contains(p)
  def sunk(guesses: Set[Point]): Boolean = points.intersect(guesses) == points
  val size: Int                          = points.size
}

object Ship {

  def create(point: Point): Option[Ship] = create(point, point)

  def create(from: Point, to: Point): Option[Ship] =
    if (from.x == to.x) {
      val range = math.min(from.y, to.y) to math.max(from.y, to.y)
      Ship(range.map(Point(from.x, _))(breakOut)).some
    } else if (from.y == to.y) {
      val range = math.min(from.x, to.x) to math.max(from.x, to.x)
      Ship(range.map(Point(_, from.y))(breakOut)).some
    } else
      None
}
