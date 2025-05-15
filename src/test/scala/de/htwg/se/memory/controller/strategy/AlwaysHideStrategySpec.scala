package de.htwg.se.memory.controller.strategy

import de.htwg.se.memory.controller.Controller
import de.htwg.se.memory.model.{Board, Card, Game, Player}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class AlwaysHideStrategySpec extends AnyWordSpec with Matchers {

  "An AlwaysHideStrategy" should {

    "always hide matching cards and switch to the next player" in {
      // Vorbereitungen
      val cards = List(Card("A", isRevealed = true), Card("A", isRevealed = true))
      val board = Board(cards)
      val players = List(Player("P1"), Player("P2"))
      val game = Game(board, players, currentPlayerIndex = 0, selectedIndices = List(0, 1))

      val controller = new Controller(game)
      val strategy = new AlwaysHideStrategy

      // Aktion
      strategy.handleMatch(controller, 0, 1)

      // Erwartung: beide Karten wieder verdeckt
      controller.gameState.board.cards(0).isRevealed shouldBe false
      controller.gameState.board.cards(1).isRevealed shouldBe false

      // Erwartung: Spielerwechsel
      controller.gameState.currentPlayerIndex shouldBe 1

      // Erwartung: keine Karten mehr ausgewählt
      controller.gameState.selectedIndices shouldBe empty
    }
  }
}
