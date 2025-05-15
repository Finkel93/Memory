import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.memory.model.{Card, Board, Game, Player}
import de.htwg.se.memory.view.InputHelper
import de.htwg.se.memory.view.Tui
import de.htwg.se.memory.controller.Controller
import java.io.StringReader

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream, InputStream}

class InputHelperSpec extends AnyWordSpec with Matchers {

  "InputHelper" should {

    "check wether index is in Bounds" in {
      val board = Board(List(Card("A"), Card("B"), Card("A"), Card("B")))
      val players = List(Player("Spieler 1"))
      val game = Game(board, players)

      // Test mit einem gültigen Index
      InputHelper.isValidInput(0, game) shouldBe true
      InputHelper.isValidInput(5, game) shouldBe false
    }

    "check wether card at index is in already revealed" in {
      val board = Board(List(Card("A", true), Card("B"), Card("A"), Card("B")))
      val players = List(Player("Spieler 1"))
      val game = Game(board, players)

      // Test mit einem gültigen Index
      InputHelper.isValidInput(0, game) shouldBe false
      InputHelper.isValidInput(1, game) shouldBe true
    }

    "check wether the card is already selected" in {
      val board = Board(List(Card("A"), Card("B"), Card("A"), Card("B")))
      val players = List(Player("Spieler 1"))
      val game = Game(board, players, 0, List(1))

      // Test mit einem gültigen Index
      InputHelper.isValidInput(2, game) shouldBe true
      InputHelper.isValidInput(1, game) shouldBe false
    }

    "return a valid input when input is within bounds and not revealed" in {
      val cards = List(Card("A"), Card("B"), Card("C"), Card("D"))
      val board = Board(cards)
      val players = List(Player("Anna"), Player("Ben"))
      val game = Game(board, players)

      val result = InputHelper.getInput("Erste Karte wählen: ", game, () => "0")

      result should be(0)
    }

    "retry after NumberFormatException and accept the second valid input" in {
      val cards = List(Card("A"), Card("B"), Card("C"), Card("D")) // index 2 gültig
      val board = Board(cards)
      val players = List(Player("Anna"))
      val game = Game(board, players)

      // Simuliere zwei Eingaben: zuerst "abc", dann "2"
      val inputs = Iterator("abc", "2")
      val readLineFunc = () => inputs.next()

      val result = InputHelper.getInput("Karte wählen: ", game, readLineFunc)

      result should be (2)
    }

    "retry after invalid index and accept the second valid input" in {
      val cards = List(Card("A"), Card("B"), Card("C"), Card("D")) // index 2 gültig
      val board = Board(cards)
      val players = List(Player("Anna"))
      val game = Game(board, players)

      // Simuliere zwei Eingaben: zuerst "abc", dann "2"
      val inputs = Iterator("5", "2")
      val readLineFunc = () => inputs.next()

      val result = InputHelper.getInput("Karte wählen: ", game, readLineFunc)

      result should be (2)
    }
  }
}

class TuiSpec extends AnyWordSpec with Matchers {

  class TestTui(controller: Controller, inputs: Iterator[String]) extends Tui(controller) {
    override def handleInput(): Unit = {
      if (inputs.hasNext) {
        val index = InputHelper.getInput("Index: ", controller.gameState, () => inputs.next())
        controller.selectCard(index)
        if (controller.gameState.selectedIndices.size == 2) {
          controller.nextTurn()
        }
      }
    }
  }




  "Tui" should {

    "run through a game" in {
      val board = Board(List(Card("A"), Card("A"))) // nur ein Kartenpaar
      val players = List(Player("Anna"), Player("Ben"))
      val game = Game(board, players)
      val controller = new Controller(game)

      val inputs = Iterator("0", "1") // Richtige Kartenwahl -> Match
      val tui = new TestTui(controller, inputs)

      tui.run()

      controller.isGameOver shouldBe true
      val winners = controller.getWinners
      winners should have size 1
      winners.head.name shouldBe "Anna"
    }

    "do nothing if game is already over" in {
      val board = Board(List(Card("A", true), Card("A", true))) // Spiel vorbei
      val players = List(Player("Anna", 1), Player("Ben"))
      val game = Game(board, players)
      val controller = new Controller(game)

      val tui = new Tui(controller)

      // Kopie des aktuellen Zustands zum Vergleich
      val originalState = controller.gameState

      // Führe run() aus – es sollte keinen Effekt haben, weil das Spiel vorbei ist
      tui.run()

      // Der Spielzustand sollte unverändert sein
      controller.gameState shouldBe originalState

    }


    "select the first card if none selected yet" in {
      val board = Board(List(Card("A")))
      val players = List(Player("Fred"))
      val game = Game(board, players)
      val controller = new Controller(game)
      val tui = new Tui(controller)

      Console.withIn(new StringReader("0\n")) {
        tui.handleInput()
      }

      controller.gameState.selectedIndices.size shouldBe 1
    }

    "select the second card if first selected yet" in {
      val board = Board(List(Card("A", true), Card("A")))
      val players = List(Player("Fred"))
      val game = Game(board, players, 0, List(0))
      val controller = new Controller(game)

      val tui = new Tui(controller)

      Console.withIn(new StringReader("1\n")) {
        tui.handleInput()
      }

      controller.gameState.selectedIndices.size shouldBe 2  // da gameOver, liste nicht mehr auf leer gesetzt
    }

    "do nothing if two cards are already selected" in {
      val cards = List(Card("A", true), Card("A", true))
      val board = Board(cards)
      val players = List(Player("Anna", 1), Player("Ben"))
      val game = Game(board, players, selectedIndices = List(0, 1)) // bereits zwei Karten gewählt
      val controller = new Controller(game)
      val tui = new Tui(controller)

      val originalState = controller.gameState

      // Aufruf von handleInput — sollte nichts tun
      tui.handleInput()

      // Zustand unverändert
      controller.gameState shouldBe originalState
    }
  }
}

