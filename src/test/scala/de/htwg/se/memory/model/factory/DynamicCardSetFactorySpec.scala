package de.htwg.se.memory.factory

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.memory.model.Card

class DynamicCardSetFactorySpec extends AnyWordSpec with Matchers {

  "A DynamicCardSetFactory" should {

    "create the correct number of cards" in {
      val pairCount = 5
      val factory = new DynamicCardSetFactory(pairCount)
      val cards = factory.createCards()

      cards.length shouldBe 2 * pairCount
    }

    "create pairs of cards with matching values" in {
      val pairCount = 3
      val factory = new DynamicCardSetFactory(pairCount)
      val cards = factory.createCards()

      val values = cards.map(_.value)

      values.distinct.foreach { v =>
        values.count(_ == v) shouldBe 2
      }
    }

    "only use values from A to Z" in {
      val pairCount = 10
      val factory = new DynamicCardSetFactory(pairCount)
      val cards = factory.createCards()

      val validValues = ('A' to 'Z').map(_.toString)
      val values = cards.map(_.value)

      values.forall(validValues.contains) shouldBe true
    }
  }
}
