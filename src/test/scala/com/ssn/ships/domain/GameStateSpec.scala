//package com.ssn.ships.domain
//
//import cats.effect.IO
//import com.ssn.ships.game.{FixedPlayer, GameInterpreter, HumanPlayer, RandomPlayer}
//import org.scalatest.{FlatSpec, Inside}
//
//import scala.collection.breakOut
//
//class GameStateSpec extends FlatSpec with Inside {
//
//  behavior of s"GameState"
//
//  it should s"player should win if he's starting and always hits" in {
//
//    val interpreter = new GameInterpreter[IO]
//    val result = interpreter.play(
//      new HumanPlayer[IO](playerAShips),
//      new FixedPlayer[IO](playerBShips, continuousHits(playerAShips))
////      new RandomPlayer[IO](playerAShips)
//    )
//
//    println(result.unsafeRunSync())
//
//  }
//
//  val playerAShips = Set(
//    Ship.create(Point(0, 0)),
//    Ship.create(Point(2, 0), Point(2, 1)),
//    Ship.create(Point(4, 0), Point(4, 2)),
//    Ship.create(Point(6, 0), Point(6, 3))
//  ).flatten
//
//  val playerBShips = Set(Ship.create(Point(0, 0))).flatten
//
//  val miss = Point(3, 3)
//
//  private def continuousHits(target: Set[Ship]): List[Point] = target.flatMap(_.points)(breakOut)
//}
