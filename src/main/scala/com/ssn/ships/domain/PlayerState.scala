package com.ssn.ships.domain

case class PlayerState(ships: PlayerShips, guesses: Set[Point]) {
  def enemyWon(enemyShots: Set[Point]): Boolean =
    ships.allPoints.intersect(enemyShots) == ships.allPoints
  def updateGuesses(updated: Set[Point]): PlayerState =
    copy(guesses = updated)
}
