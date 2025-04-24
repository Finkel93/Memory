import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class GameControllerSpec extends AnyWordSpec with Matchers {

  "A GameController" should {

    "reveal cards correctly and handle invalid selections" in {
      val cards = List(Card("A"), Card("B"))
      val board = Board(cards)
      val players = List(Player("P1"), Player("P2"))
      val controller = new GameController(Game(board, players))

      controller.selectCard(0) shouldBe Right(())

      // Versuch, dieselbe Karte erneut zu wählen
      controller.selectCard(0) shouldBe Left("Ungültige Auswahl. Karte entweder schon aufgedeckt oder doppelt gewählt.")
    }

    "do nothing in nextTurn if less than two cards are selected" in {
      val cards = List(Card("A"), Card("B"))
      val board = Board(cards)
      val players = List(Player("P1"), Player("P2"))
      val controller = new GameController(Game(board, players, currentPlayerIndex = 0))

      controller.selectCard(0) // nur eine Karte

      // Spieler bleibt gleich
      controller.nextTurn()
      controller.game.currentPlayerIndex shouldBe 0

      // Karte bleibt aufgedeckt
      controller.game.board.cards(0).isRevealed shouldBe true

      // selectedIndices bleibt gleich
      controller.game.selectedIndices should contain only 0
    }


    "award a point if two selected cards match" in {
      val cards = List(Card("X"), Card("X"))
      val board = Board(cards)
      val players = List(Player("P1"), Player("P2"))
      val controller = new GameController(Game(board, players))

      controller.selectCard(0)
      controller.selectCard(1)
      controller.nextTurn()

      // Spieler 0 bleibt dran und bekommt Punkt
      controller.currentPlayer.name shouldBe "P1"
      controller.game.players(0).score shouldBe 1
    }

    "switch player if two selected cards do not match" in {
      val cards = List(Card("X"), Card("Y"))
      val board = Board(cards)
      val players = List(Player("P1"), Player("P2"))
      val controller = new GameController(Game(board, players))

      controller.selectCard(0)
      controller.selectCard(1)
      controller.nextTurn()

      controller.currentPlayer.name shouldBe "P2"
      controller.game.players(0).score shouldBe 0
      controller.game.board.cards(0).isRevealed shouldBe false
      controller.game.board.cards(1).isRevealed shouldBe false
    }

    "return correct winners after game is over" in {
      val cards = List(Card("A", true), Card("A", true))
      val players = List(Player("P1", 2), Player("P2", 1))
      val board = Board(cards)
      val controller = new GameController(Game(board, players))

      controller.isGameOver shouldBe true
      val winners = controller.getWinners
      winners.size shouldBe 1
      winners.head.name shouldBe "P1"
    }

    "print correct winners after game is over" in {
      val cards = List(Card("A", true), Card("A", true))
      val players = List(Player("P1", 2), Player("P2", 1))
      val board = Board(cards)
      val controller = new GameController(Game(board, players))

      controller.isGameOver shouldBe true
      val winners = controller.getWinners
      val output = controller.printResult(winners)
      output should be ("Der Gewinner ist: P1")
    }

    "print tie in cas of tie after game is over" in {
      val cards = List(Card("A", true), Card("A", true))
      val players = List(Player("P1", 1), Player("P2", 1))
      val board = Board(cards)
      val controller = new GameController(Game(board, players))

      controller.isGameOver shouldBe true
      val winners = controller.getWinners
      val output = controller.printResult(winners)
      output should be ("Unentschieden!")
    }


    "show the correct board view" in {
      val cards = List(Card("A", true), Card("B", false))
      val board = Board(cards)
      val controller = new GameController(Game(board, List(Player("P1"))))

      val view = controller.boardView
      view should include("A")
      view should include("?")
    }
  }
}
