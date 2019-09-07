package com.ssn.ships

import cats.implicits._
import cats.effect._
import com.ssn.ships.domain._
import com.ssn.ships.player._
import org.scalatest.{AsyncFlatSpec, Inside}

import scala.concurrent.ExecutionContext.global

class MainSpec extends AsyncFlatSpec with Inside {

  behavior of s"GameState"

  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  implicit val timer: Timer[IO]     = IO.timer(global)

  //TODO: add more tests here + unit test in general ;)
  //Probably not so good to have any randomness in tests.
  //Here it will fail eventually but chance is slight and it's just for demonstration purposes.

  it should s"ai player should (almost!) always win with random" in {
    (for {
      results <- multipleGames(10, randomVsAI)
    } yield {
      assert(results.toSet == Set(PlayerB))
    }).unsafeToFuture()
  }

  it should s"ai vs ai should be more even" in {
    (for {
      results <- multipleGames(20, AIVsAI)
    } yield {
      assert(results.toSet == Set(PlayerA, PlayerB))
    }).unsafeToFuture()
  }

  private def multipleGames(no: Int, game: IO[(Player, GameState)]): IO[List[Player]] =
    (1 to no).toList
      .traverse(
        i =>
          console.printLine[IO]("======================") *>
            console.printLine[IO](s"Game no $i") *>
            console.printLine[IO]("======================") *>
            game.map { case (winner, _) => winner }
      )

  private val randomVsAI = for {
    ai <- AIPlayer.make[IO]
    random = new RandomPlayer[IO]
    result <- Main.run0(random, ai)
  } yield result

  private val AIVsAI = for {
    ai1    <- AIPlayer.make[IO]
    ai2    <- AIPlayer.make[IO]
    result <- Main.run0(ai1, ai2)
  } yield result

}
