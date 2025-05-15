package de.htwg.se.memory.factory

import de.htwg.se.memory.model.Card
import scala.util.Random

class DynamicCardSetFactory(pairCount: Int) extends CardSetFactory {
  override def createCards(): List[Card] = {
    val baseValues = ('A' to 'Z').map(_.toString)
    val selectedValues = baseValues.take(pairCount)
    val duplicated = selectedValues ++ selectedValues
    Random.shuffle(duplicated).map(Card(_)).toList
  }
}
