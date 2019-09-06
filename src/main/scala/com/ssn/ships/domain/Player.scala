package com.ssn.ships.domain

sealed abstract class Player extends Product with Serializable {
  def other: Player
}

case object PlayerA extends Player {
  override val other: Player = PlayerB
}
case object PlayerB extends Player {
  override val other: Player = PlayerA
}
