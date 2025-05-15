package de.htwg.se.memory.factory

import de.htwg.se.memory.model.Card

trait CardSetFactory {
  def createCards(): List[Card]
}
