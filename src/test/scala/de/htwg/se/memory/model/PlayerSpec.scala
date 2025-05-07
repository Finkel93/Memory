package de.htwg.se.memory.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class PlayerSpec extends AnyWordSpec with Matchers {

  "A Player" should {

    "start with a score of 0" in {
      val player = Player("Anna")
      player.score shouldBe 0
    }

    "increment score when a point is added" in {
      val player = Player("Anna")
      val updatedPlayer = player.addPoint()

      updatedPlayer.score shouldBe 1
    }

    "return a new player with an updated score after adding a point" in {
      val player = Player("Anna", 2)
      val updatedPlayer = player.addPoint()

      updatedPlayer.score shouldBe 3
      updatedPlayer.name shouldBe player.name
    }
  }
}

