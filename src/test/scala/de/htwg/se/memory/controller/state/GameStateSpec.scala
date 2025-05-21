package de.htwg.se.memory.controller.state

import de.htwg.se.memory.controller.Controller
import de.htwg.se.memory.model.{Board, Card, Game, Player}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.{ByteArrayOutputStream, PrintStream}

class GameStateSpec extends AnyWordSpec with Matchers {

  // Hilfsmethode, um Konsolenausgaben abzufangen
  def captureOutput(block: => Unit): String = {
    val baos = new ByteArrayOutputStream()
    val ps = new PrintStream(baos)
    Console.withOut(ps) {
      block
    }
    baos.toString
  }


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

    "WaitingFirstCardState should not change state on invalid input" in {
      val cards = List(Card("A", true), Card("B", false))
      val controller = new Controller(Game(Board(cards), List(Player("Anna"), Player("Ben"))))
      controller.setState(new WaitingFirstCardState)

      val beforeState = controller.getStateName
      controller.state.handleInput(0, controller)
      val afterState = controller.getStateName

      // Der Zustand sollte sich nicht geändert haben
      afterState shouldBe beforeState
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
      val cards = List(Card("A", true), Card("A"))
      val controller = createControllerWithState(new WaitingSecondCardState, cards, selected = List(0))

      // Hier müssen wir das Spiel als beendet simulieren.
      // Da Controller.isGameOver vermutlich von Game abhängt, kannst du ggf.
      // isGameOver in Controller mocken oder Game entsprechend vorbereiten.

      // Für einfachen Test erzwingen wir isGameOver manuell:
      /*val controllerWithGameOver = new Controller(controller.gameState) {
        override def isGameOver: Boolean = true
      }

       */
      //controllerWithGameOver.setState(new WaitingSecondCardState)

      controller.state.handleInput(1, controller)

      controller.state.name shouldBe "GameOver"
    }

    "WaitingFirstCardState should print an error message on invalid input" in {


      val outContent = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outContent)) {
        // Karte 0 ist bereits aufgedeckt ⇒ IllegalArgumentException erwartet
        val cards = List(Card("A", true), Card("B", false))
        val controller = new Controller(Game(Board(cards), List(Player("Anna"), Player("Ben"))))
        controller.setState(new WaitingFirstCardState)

        controller.state.handleInput(0, controller)
      }

      val output = outContent.toString.trim
      output should include("Ungültige Auswahl")
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


