package com.ssn.ships.domain

import org.scalatest.FlatSpec
import org.scalatest.prop.TableDrivenPropertyChecks

class PlayerShipsSpec extends FlatSpec with TableDrivenPropertyChecks {

  behavior of "PlayerShips validator"

  forAll(examples)(
    (desc, ships, expectedResult) =>
      it should s"return $expectedResult when $desc" in {
        val result = PlayerShips.validate(PlayerShips(ships.flatten))
        assert(result == expectedResult)
      }
  )

  lazy val examples = Table(
    ("description", "ships", "isValid"),
    (
      "just a single ship is placed",
      Set(
        Ship.create(Point(0, 0))
      ),
      true
    ),
    (
      "two distant ships are placed",
      Set(
        Ship.create(Point(0, 0), Point(2, 0)),
        Ship.create(Point(0, 2), Point(3, 2))
      ),
      true
    ),
    (
      "there are three ships and they respect the proximity zone",
      Set(
        Ship.create(Point(0, 0), Point(2, 0)),
        Ship.create(Point(0, 2), Point(3, 2)),
        Ship.create(Point(0, 4), Point(0, 6))
      ),
      true
    ),
    (
      "there are three ships but they violate the proximity zone",
      Set(
        Ship.create(Point(0, 0), Point(2, 0)),
        Ship.create(Point(0, 2), Point(3, 2)),
        Ship.create(Point(0, 3), Point(0, 5))
      ),
      false
    )
  )
}
