package de.htwg.se.memory.controller.state

import de.htwg.se.memory.controller.Controller
import de.htwg.se.memory.model.fileIO.FileIOInterface
import de.htwg.se.memory.model.{Board, Card, Game, ModelInterface, Player}
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
    baos.toString.trim
  }

  def createControllerWithState(state: GameState, cards: List[Card], currentPlayerIndex: Int = 0, selected: List[Int] = Nil): Controller = {
    val board = Board(cards)
    val players = List(Player("P1"), Player("P2"))
    val game = Game(board, players, currentPlayerIndex = currentPlayerIndex, selectedIndices = selected)

    val dummyFileIO = new FileIOInterface {
      override def save(game: ModelInterface): Unit = {}

      override def load(): ModelInterface = game  // Korrekte Signatur mit () und Rückgabe
    }
    val controller = new Controller(game, dummyFileIO)
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

    "not change state on invalid input" in {
      val cards = List(Card("A", true), Card("B", false))
      val controller = createControllerWithState(new WaitingFirstCardState, cards)

      val beforeState = controller.getStateName
      val output = captureOutput {
        controller.state.handleInput(0, controller)
      }
      val afterState = controller.getStateName

      afterState shouldBe beforeState
      output should include ("Ungültige Auswahl")
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
      val cards = List(Card("A", true), Card("A", false))
      // Hier setzen wir selected = List(0), damit zweite Karte gewählt wird und Spiel als vorbei angenommen wird
      val controller = createControllerWithState(new WaitingSecondCardState, cards, selected = List(0))
      // Manuell Spielende simulieren, wenn nötig

      // Wir nehmen an, dass isGameOver true wird, wenn Karten übereinstimmen und alle Karten aufgedeckt sind.
      // Falls nötig, kannst du das Modell für diesen Test anpassen.

      controller.state.handleInput(1, controller)

      controller.state.name shouldBe "GameOver"
    }

    "print an error message on invalid input" in {
      val cards = List(Card("A", true), Card("B", false))
      val controller = createControllerWithState(new WaitingSecondCardState, cards)

      val output = captureOutput {
        controller.state.handleInput(0, controller)
      }

      output should include("Ungültige Auswahl")
    }
  }

  "GameOverState" should {
    "ignore any input" in {
      val cards = List(Card("A", true), Card("A", true))
      val controller = createControllerWithState(new GameOverState, cards)

      controller.state.name shouldBe "GameOver"

      noException should be thrownBy controller.state.handleInput(0, controller)

      controller.state.name shouldBe "GameOver"
    }
  }
}
