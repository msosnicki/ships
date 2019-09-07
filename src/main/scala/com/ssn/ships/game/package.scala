package com.ssn.ships

import com.ssn.ships.domain.Point
import fs2._

package object game {

  type Actions[F[_]] = Stream[F, Point]

  /**
    * Should be taken from config perhaps?
    */
  val ShipSizes: Set[Int] = Set(1, 2, 3, 4)

}
