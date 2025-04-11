import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers


class BoardSpec extends AnyWordSpec with Matchers {

  "A Board" should {

    "reveal a card correctly" in {
      val board = Board(List(Card("A"), Card("B"), Card("A"), Card("B")))
      val updatedBoard = board.revealCard(0)

      updatedBoard.cards(0).isRevealed shouldBe true
      updatedBoard.cards(0).value shouldBe "A"
    }

    "not allow revealing an already revealed card" in {
      val board = Board(List(Card("A", true), Card("B", false)))
      an[IllegalArgumentException] should be thrownBy {
        board.revealCard(0)
      }
    }

    "hide a card correctly" in {
      val board = Board(List(Card("A", true), Card("B"), Card("A"), Card("B")))
      val updatedBoard = board.hideCard(0)

      updatedBoard.cards(0).isRevealed shouldBe false
      updatedBoard.cards(0).value shouldBe "A"
    }

    "not allow hiding an already hidden card" in {
      val board = Board(List(Card("A", false), Card("B", false)))
      an[IllegalArgumentException] should be thrownBy {
        board.hideCard(0)
      }
    }


    "correctly detect when the game is over" in {
      val board = Board(List(Card("A", true), Card("B", true), Card("A", true), Card("B", true)))
      board.isGameOver shouldBe true
    }

    "detect when the game is not over" in {
      val board = Board(List(Card("A", true), Card("B", false), Card("A", false), Card("B", false)))
      board.isGameOver shouldBe false
    }

    "correctly prints the board" in {
      val board = Board(List(Card("A", true), Card("B", false), Card("A", false), Card("B", false)))
      val result = board.displayCards(board.cards)
      result should be ("[ 0: A ][ 1: ? ][ 2: ? ][ 3: ? ]")

    }
  }
}
