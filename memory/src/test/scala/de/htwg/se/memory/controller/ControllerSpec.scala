package de.htwg.se.memory.controller

import de.htwg.se.memory.model.{Card, Board, Game, Player}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ControllerSpec extends AnyWordSpec with Matchers {

  "A Controller" should {

    "select a card and reveal it" in {
      val board = Board(List(Card("A"), Card("B"), Card("A"), Card("B")))
      val players = List(Player("Anna"), Player("Ben"))
      var game = Game(board, players)
      val controller = new Controller(game)

      // Karte 0 auswählen
      controller.selectCard(0)

      controller.gameState.board.cards(0).isRevealed shouldBe true
      controller.gameState.selectedIndices should contain(0)
    }

    "switch to the next player when two different cards are selected" in {
      val board = Board(List(Card("A"), Card("A"), Card("B"), Card("B")))
      val players = List(Player("Anna"), Player("Ben"))
      var game = Game(board, players)
      val controller = new Controller(game)

      // Karten 0 und 1 auswählen
      controller.selectCard(0)
      controller.selectCard(2)
      controller.nextTurn()
      // Spieler wechseln
      controller.gameState.currentPlayerIndex shouldBe 1 // Nächster Spieler (Ben) ist jetzt dran
    }

    "not allow selecting a card that is already revealed" in {
      val board = Board(List(Card("A", true), Card("B"), Card("A"), Card("B")))
      val players = List(Player("Anna"), Player("Ben"))
      var game = Game(board, players)
      val controller = new Controller(game)

      // Karte 0 ist bereits aufgedeckt
      an[IllegalArgumentException] should be thrownBy controller.selectCard(0)
    }

    "keep track of the current player" in {
      val board = Board(List(Card("A"), Card("A"), Card("B"), Card("B")))
      val players = List(Player("Anna"), Player("Ben"))
      var game = Game(board, players)
      val controller = new Controller(game)

      controller.currentPlayer.name shouldBe "Anna"
    }

    "keep the player on the same turn if the cards match" in {
      val board = Board(List(Card("A"), Card("A"), Card("B"), Card("B")))
      val players = List(Player("Anna"), Player("Ben"))
      var game = Game(board, players)
      val controller = new Controller(game)

      // Karten 0 und 1 auswählen
      controller.selectCard(0)
      controller.selectCard(1)
      controller.nextTurn()
      // Der Spieler Anna bleibt am Zug
      game.currentPlayerIndex shouldBe 0
    }

    "correctly detect if the game is over" in {
      val board = Board(List(
        Card("X", true), Card("X", true),
        Card("Y", true), Card("Y", true)
      ))
      val players = List(Player("Anna", 2), Player("Ben", 0))
      val game = Game(board, players)
      val controller = new Controller(game)

      controller.isGameOver shouldBe true
    }

    "update the board view correctly" in {
      val board = Board(List(Card("X"), Card("Y"), Card("Z"), Card("W")))
      val players = List(Player("Anna"), Player("Ben"))
      var game = Game(board, players)
      val controller = new Controller(game)

      controller.selectCard(0)
      controller.boardView should include ("X")
      controller.selectCard(1)
      controller.boardView should include ("Y")
    }

    "get the correct winner" in {
      val board = Board(List(
        Card("X", true), Card("X", true),
        Card("Y", true), Card("Y", true)
      ))
      val players = List(Player("Anna", 2), Player("Ben", 0))
      val game = Game(board, players)
      val controller = new Controller(game)

      controller.getWinners.map(_.name) should contain("Anna")
    }

    "handle a game with a tie" in {
      val board = Board(List(
        Card("X", true), Card("X", true),
        Card("Y", true), Card("Y", true)
      ))
      val players = List(Player("Anna", 1), Player("Ben", 1))
      val game = Game(board, players)
      val controller = new Controller(game)

      controller.getWinners.map(_.name) should contain allOf ("Anna", "Ben")
    }

    "wait for a second revealed Card" in {
      val board = Board(List(Card("X"), Card("Y"), Card("Z"), Card("W")))
      val players = List(Player("Anna"), Player("Ben"))
      var game = Game(board, players)
      val controller = new Controller(game)

      controller.selectCard(0)
      val initialController = controller
      controller.nextTurn()

      controller shouldBe initialController

    }
  }
}
