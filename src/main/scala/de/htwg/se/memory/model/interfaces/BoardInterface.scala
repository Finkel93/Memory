package de.htwg.se.memory.model

trait BoardInterface{
  def revealCard(index: Int): BoardInterface
  def hideCard(index: Int): BoardInterface
  def isGameOver: Boolean
  def displayCards(cards: List[CardInterface]): String
  def cards: List[CardInterface]

}
