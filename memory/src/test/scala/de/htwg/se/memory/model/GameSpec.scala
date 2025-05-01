package de.htwg.se.memory.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class GameSpec extends AnyWordSpec with Matchers {

  "A Game" should {

    "reveal a card correctly on first selection" in {
      val board = Board(List(Card("A"), Card("B"), Card("A"), Card("B")))
      val players = List(Player("Anna"), Player("Ben"))
      val game = Game(board, players)

      val updatedGame = game.selectCard(0)

      updatedGame.board.cards(0).isRevealed shouldBe true
      //updatedGame.selectedCardIndex shouldBe Some(0)
    }

    "reveal a second card and get match" in {
      val board = Board(List(Card("A"), Card("A"), Card("B"), Card("B")))
      val players = List(Player("Anna"), Player("Ben"))
      var game = Game(board, players)

      game = game.selectCard(0)
      game = game.selectCard(1)

      val updatedPlayer = game.players(0).addPoint()
      updatedPlayer.score shouldBe 1
      game.currentPlayerIndex shouldBe 0 // same player plays again
      game.board.cards(0).isRevealed shouldBe true
      game.board.cards(1).isRevealed shouldBe true
    }


    "switch to the next player if no match is found" in {
      val board = Board(List(Card("A"), Card("B"), Card("A"), Card("B")))
      val players = List(Player("Anna"), Player("Ben"))
      var game = Game(board, players)

      game = game.selectCard(0)
      game = game.selectCard(1)
      val nextIndex = (game.currentPlayerIndex + 1) % players.length

      val updatedGame = Game(game.board.hideCard(0), players)
      val latestGame = Game(updatedGame.board.hideCard(1), players)

      game.players(0).score shouldBe 0
      nextIndex shouldBe 1
      latestGame.board.cards(0).isRevealed shouldBe false
      latestGame.board.cards(1).isRevealed shouldBe false
    }

    "detect when the game is over" in {
      val board = Board(List(
        Card("X", true), Card("X", true),
        Card("Y", true), Card("Y", true)
      ))
      val players = List(Player("Anna", 2), Player("Ben", 0))
      val game = Game(board, players)

      game.isGameOver shouldBe true
    }

    "determine the winner correctly" in {
      val board = Board(List(
        Card("X", true), Card("X", true),
        Card("Y", true), Card("Y", true)
      ))
      val players = List(Player("Anna", 2), Player("Ben"))
      val game = Game(board, players)

      val winners = game.getWinners
      winners should have size 1
      winners.head.name shouldBe "Anna"
    }

    "detect a tie if multiple players have the same score" in {
      val board = Board(List(
        Card("X", true), Card("X", true),
        Card("Y", true), Card("Y", true)
      ))
      val players = List(Player("Anna", 1), Player("Ben", 1))
      val game = Game(board, players)

      val winners = game.getWinners
      winners.map(_.name) should contain allOf("Anna", "Ben")
    }

    "block wrong input" in {
      val board = Board(List(Card("A", true), Card("A"), Card("B"), Card("B")))
      val players = List(Player("Anna"), Player("Ben"))
      val game = Game(board, players)
      an[IllegalArgumentException] should be thrownBy {
        game.selectCard(0)
      }
    }

    "only crown winners when its over" in {
      val board = Board(List(Card("A", true), Card("A"), Card("B"), Card("B")))
      val players = List(Player("Anna"), Player("Ben"))
      val game = Game(board, players)
      val winners = game.getWinners
      winners.length shouldBe 0
    }

    "correctly print the result of the game" in {
      // Testfall 1: Ein Spieler gewinnt
      val board1 = Board(List(
        Card("X", true), Card("X", true),
        Card("Y", true), Card("Y", true)
      ))
      val players1 = List(Player("Anna", 2), Player("Ben", 0))
      val game1 = Game(board1, players1)

      val winners1 = game1.getWinners
      val result1 = game1.printResult(winners1)

      result1 shouldBe "Der Gewinner ist: Anna"

      // Testfall 2: Unentschieden (mehrere Spieler mit der gleichen Punktzahl)
      val board2 = Board(List(
        Card("X", true), Card("X", true),
        Card("Y", true), Card("Y", true)
      ))
      val players2 = List(Player("Anna", 1), Player("Ben", 1))
      val game2 = Game(board2, players2)

      val winners2 = game2.getWinners
      val result2 = game2.printResult(winners2)

      result2 shouldBe "Unentschieden!"
    }
  }
}
