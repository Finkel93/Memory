package de.htwg.se.memory.model.fileIO

import de.htwg.se.memory.model.ModelInterface

trait FileIOInterface {
  def save(game: ModelInterface): Unit
  def load(): ModelInterface
}
