package com.ssn.ships

import com.ssn.ships.domain.Point
import fs2._

package object game {

  type Actions[F[_]] = Stream[F, Point]

}
