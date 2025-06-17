package de.htwg.se.memory.model

trait PlayerInterface {
  def name: String
  def score: Int
  def addPoint(): PlayerInterface
}
