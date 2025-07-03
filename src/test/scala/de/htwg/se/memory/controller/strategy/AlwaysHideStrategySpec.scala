package de.htwg.se.memory.controller.strategy

import de.htwg.se.memory.controller.Controller
import de.htwg.se.memory.model.{Board, Card, Game, Player}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable

class AlwaysHideStrategySpec extends AnyWordSpec with Matchers {

  "AlwaysHideStrategy" should {

    "hide the matched cards, switch to next player and notify observers" in {
      // Karten vorbereiten, beide aufgedeckt (für das Verstecken)
      val cards = List(
        Card("A", isRevealed = true),
        Card("A", isRevealed = true),
        Card("B", isRevealed = false)
      )
      val board = Board(cards)
      val players = List(Player("Alice"), Player("Bob"))
      val game = Game(board, players, currentPlayerIndex = 0, selectedIndices = List(0, 1))

      // Dummy FileIO (leer, nicht wichtig hier)
      val dummyFileIO = new de.htwg.se.memory.model.fileIO.FileIOInterface {
        override def save(game: de.htwg.se.memory.model.ModelInterface): Unit = {}
        override def load(): de.htwg.se.memory.model.ModelInterface = game
      }

      val controller = new Controller(game, dummyFileIO)

      // Setze Strategy
      val strategy = new AlwaysHideStrategy

      // Flag, ob Observer benachrichtigt wurde
      var notified = false
      controller.add(new de.htwg.se.memory.util.Observer {
        override def update: Unit = notified = true
      })

      // Strategie anwenden
      strategy.handleMatch(controller, 0, 1)

      // Karten 0 und 1 sollten versteckt sein (isRevealed == false)
      controller.gameState.board.cards(0).isRevealed shouldBe false
      controller.gameState.board.cards(1).isRevealed shouldBe false

      // Spielerindex sollte gewechselt haben
      controller.gameState.currentPlayerIndex shouldBe 1

      // Auswahl sollte geleert sein
      controller.gameState.selectedIndices shouldBe empty

      // Observer sollten benachrichtigt worden sein
      notified shouldBe true
    }
  }
}
