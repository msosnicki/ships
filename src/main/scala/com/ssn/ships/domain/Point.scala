package com.ssn.ships.domain

import atto._
import Atto._
import atto.ParseResult.Done

//TODO: also here could be validated on construction (same story with ships)
case class Point(x: Int, y: Int)

object Point {

  def isValid(point: Point): Boolean =
    point.x >= 0 && point.x <= 9 && point.y >= 0 && point.y <= 9

  def pointsAtDistance(point: Point, distance: Int, horizontal: Boolean): List[Point] =
    if (horizontal)
      List(Point(point.x - distance, point.y), Point(point.x + distance, point.y)).filter(isValid)
    else
      List(Point(point.x, point.y - distance), Point(point.x, point.y + distance)).filter(isValid)

  private val parser = charRange('A' to 'J') ~ orElse(string("10"), digit.filter(_ != '0').map(_.toString))

  /**
    * Parses so that A1 -> Point(0, 0) and C8 -> Point(3, 8)
    *  Allowed only up to J10 -> Point(9, 9)
    * @param str
    * @return
    */
  def parse(str: String): Option[Point] =
    parser
      .parseOnly(str) match {
      case Done(rem, (xLetter, y)) if rem.isEmpty => Some(Point(xLetter.toInt - 65, y.toInt - 1))
      case _                                      => None
    }

}
