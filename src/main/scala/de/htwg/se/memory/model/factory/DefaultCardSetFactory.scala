package de.htwg.se.memory.factory

import de.htwg.se.memory.model.Card
import scala.util.Random

class DefaultCardSetFactory extends CardSetFactory {
  override def createCards(): List[Card] = {
    val values = List("A", "A", "B", "B", "C", "C", "D", "D")
    Random.shuffle(values).map(Card(_))
  }
}
