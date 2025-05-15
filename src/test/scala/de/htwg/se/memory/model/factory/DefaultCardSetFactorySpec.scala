package de.htwg.se.memory.factory

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.memory.model.Card

class DefaultCardSetFactorySpec extends AnyWordSpec with Matchers {

  "A DefaultCardSetFactory" should {

    "create exactly 8 cards" in {
      val factory = new DefaultCardSetFactory()
      val cards = factory.createCards()

      cards.length shouldBe 8
    }

    "contain pairs of cards with matching values" in {
      val factory = new DefaultCardSetFactory()
      val cards = factory.createCards()

      val values = cards.map(_.value)

      // Werte, die in der ursprünglichen Liste sind
      val expectedValues = List("A", "B", "C", "D")

      // Prüfe, dass jede der erwarteten Werte genau 2 mal vorkommt
      expectedValues.foreach { v =>
        values.count(_ == v) shouldBe 2
      }
    }

    "only contain values from the predefined set" in {
      val factory = new DefaultCardSetFactory()
      val cards = factory.createCards()

      val validValues = Set("A", "B", "C", "D")
      val values = cards.map(_.value)

      values.forall(validValues.contains) shouldBe true
    }
  }
}
