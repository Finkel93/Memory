package de.htwg.se.memory.model

case class Player(name: String, score: Int = 0) extends PlayerInterface {
  def addPoint(): Player = copy(score = score + 1)
}