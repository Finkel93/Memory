package de.htwg.se.memory.model

trait CardInterface {
  def reveal(): CardInterface
  def hide(): CardInterface
  def isRevealed: Boolean
  def value: String
}