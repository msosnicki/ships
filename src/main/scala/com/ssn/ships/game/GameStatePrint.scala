package com.ssn.ships.game

import com.ssn.ships.domain.{GameState, Player, PlayerShips, Point}

object GameStatePrint {
  def printShipChoice(playerShips: PlayerShips): String =
    prettyPrint(
      point =>
        if (playerShips.allPoints.contains(point))
          Some('S')
        else None
    )

  def printPlayerState(gameState: GameState, player: Player): String = {
    val guesses        = gameState.getState(player).guesses
    val targets        = gameState.getState(player.other).ships.allPoints
    val (hits, misses) = guesses.partition(targets.contains)
    printPlayerState(hits, misses)
  }

  def printPlayerState(hits: Set[Point], misses: Set[Point]): String =
    prettyPrint(
      point =>
        if (hits.contains(point)) Some('X')
        else if (misses.contains(point)) Some('O')
        else None
    )

  private def prettyPrint(char: Point => Option[Char]): String = {
    val firstRow = s"   ${('A' to 'J').mkString(" ")}"
    val rows = for {
      rowNo <- 0 to 9
      rowString = (0 to 9)
        .map(x => char(Point(x, rowNo)).getOrElse(' '))
        .mkString("%2d ".format(rowNo + 1), " ", "")
    } yield rowString
    (List(firstRow) ++ rows).mkString("\n")
  }
}
