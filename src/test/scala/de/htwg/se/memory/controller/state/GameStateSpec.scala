package de.htwg.se.memory.controller.state

import de.htwg.se.memory.controller.Controller
import de.htwg.se.memory.model.{Board, Card, Game, Player}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameStateSpec extends AnyWordSpec with Matchers {

  def createControllerWithState(state: GameState, cards: List[Card], currentPlayerIndex: Int = 0, selected: List[Int] = Nil): Controller = {
    val board = Board(cards)
    val players = List(Player("P1"), Player("P2"))
    val game = Game(board, players, currentPlayerIndex = currentPlayerIndex, selectedIndices = selected)
    val controller = new Controller(game)
    controller.setState(state)
    controller
  }

  "WaitingFirstCardState" should {
    "accept a valid first card and switch to WaitingSecondCardState" in {
      val cards = List(Card("A", false), Card("B", false))
      val controller = createControllerWithState(new WaitingFirstCardState, cards)

      controller.state.name shouldBe "WaitingFirstCard"

      controller.state.handleInput(0, controller)

      controller.gameState.selectedIndices should contain(0)
      controller.state.name shouldBe "WaitingSecondCard"
    }

    "handle invalid selection gracefully" in {
      val cards = List(Card("A", true), Card("B", false))
      val controller = createControllerWithState(new WaitingFirstCardState, cards)

      controller.state.name shouldBe "WaitingFirstCard"

      // Karte 0 ist schon aufgedeckt, Auswahl sollte fehlschlagen
      controller.state.handleInput(0, controller)

      controller.gameState.selectedIndices shouldBe empty
      controller.state.name shouldBe "WaitingFirstCard"  // bleibt gleich
    }
  }

  "WaitingSecondCardState" should {
    "accept a valid second card, perform next turn and switch state accordingly" in {
      val cards = List(Card("A", false), Card("A", false))
      val controller = createControllerWithState(new WaitingSecondCardState, cards, selected = List(0))

      controller.state.name shouldBe "WaitingSecondCard"

      controller.state.handleInput(1, controller)

      controller.gameState.selectedIndices shouldBe empty
      controller.state.name shouldBe "WaitingFirstCard"  // da Spiel nicht vorbei ist
    }

    "switch to GameOverState when game is over" in {
      // Wir simulieren Spielende: alle Karten aufgedeckt
      val cards = List(Card("A", true), Card("A"))
      val controller = createControllerWithState(new WaitingSecondCardState, cards, selected = List(0))

      controller.handleInput(1)

      // Manuell isGameOver auf true setzen
      // Normalerweise müsste Game.isGameOver korrekt implementiert sein.
      /*val controllerSpy = new Controller(controller.gameState) {
        override def isGameOver: Boolean = true
      }*/
      //controllerSpy.setState(new WaitingSecondCardState)

      //controllerSpy.state.handleInput(1, controllerSpy)

      controller.state.name shouldBe "GameOver"
    }



    "handle invalid selection gracefully" in {
      val cards = List(Card("A", true), Card("B", false))
      val controller = createControllerWithState(new WaitingSecondCardState, cards, selected = List(0))

      controller.state.handleInput(0, controller) // Karte 0 bereits ausgewählt

      controller.state.name shouldBe "WaitingSecondCard" // bleibt gleich
    }
  }

  "GameOverState" should {
    "ignore any input" in {
      val cards = List(Card("A", true), Card("A", true))
      val controller = createControllerWithState(new GameOverState, cards)

      controller.state.name shouldBe "GameOver"

      controller.state.handleInput(0, controller)  // Sollte ignoriert werden ohne Fehler

      controller.state.name shouldBe "GameOver" // bleibt GameOver
    }
  }

}
