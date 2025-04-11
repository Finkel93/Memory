import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class CardSpec extends AnyWordSpec with Matchers {

  "A Card" should {

    "be revealed when reveal() is called" in {
      val card = Card("A")
      val revealedCard = card.reveal()

      revealedCard.isRevealed should be(true)
    }

    "be hidden when hide() is called" in {
      val card = Card("A", isRevealed = true)
      val hiddenCard = card.hide()

      hiddenCard.isRevealed should be(false)
    }

    "have the correct value when created" in {
      val card = Card("A")

      card.value should be("A")
    }

    "retain the same value after calling reveal or hide" in {
      val card = Card("A")
      val revealedCard = card.reveal()

      revealedCard.value should be("A")

      val hiddenCard = revealedCard.hide()
      hiddenCard.value should be("A")
    }
  }
}
