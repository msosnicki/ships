package com.ssn.ships.domain

case class GameState(playerA: PlayerState, playerB: PlayerState, turnOf: Player) {

  def shot(player: Player, point: Point): TurnResult =
    if (player == turnOf) {
      val oldGuesses = attacker.guesses
      val newGuesses = attacker.guesses + point
      val result = defender.ships.get.find(_.contains(point)) match {
        case None                                  => Miss
        case Some(_) if oldGuesses.contains(point) => Miss
        case Some(ship) =>
          if (defender.allSunk(newGuesses)) GameWon
          else if (ship.sunk(newGuesses)) Sunk
          else Hit
      }
      val nextTurn = if (result == Miss) turnOf.other else turnOf
      val newState = createNextState(newGuesses, nextTurn)
      Some(newState, result)
    } else None

  def getState(player: Player): PlayerState =
    if (player == PlayerA) playerA else playerB

  private val (attacker, defender) = (getState(turnOf), getState(turnOf.other))

  private def createNextState(updatedGuesses: Set[Point], nextTurn: Player): GameState =
    if (turnOf == PlayerA) copy(playerA = playerA.updateGuesses(updatedGuesses), turnOf = nextTurn)
    else copy(playerB = playerB.updateGuesses(updatedGuesses), turnOf = nextTurn)

}
