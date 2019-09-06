package com.ssn.ships.domain

sealed abstract class ShotResult extends Product with Serializable

case object Miss    extends ShotResult
case object Hit     extends ShotResult
case object Sunk    extends ShotResult
case object GameWon extends ShotResult
