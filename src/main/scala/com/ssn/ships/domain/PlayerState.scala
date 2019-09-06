package com.ssn.ships.domain

case class PlayerState(ships: PlayerShips, guesses: Set[Point]) {
  def allSunk(guesses: Set[Point]): Boolean =
    ships.allPoints.intersect(guesses) == ships.allPoints
  def updateGuesses(updated: Set[Point]): PlayerState =
    copy(guesses = updated)
}
