package de.htwg.se.memory.model

case class Card(value: String, isRevealed: Boolean = false) {
  def reveal(): Card = copy(isRevealed = true)
  def hide(): Card = copy(isRevealed = false)
}