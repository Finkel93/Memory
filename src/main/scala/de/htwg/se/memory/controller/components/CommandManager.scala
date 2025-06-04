package de.htwg.se.memory.controller.components

import de.htwg.se.memory.controller.command.Command

// Kohäsive Command-Management Komponente
trait CommandManager extends EventPublisher {
  def executeCommand(command: Command): Unit
  def undo(): Unit
  def redo(): Unit
  def canUndo: Boolean
  def canRedo: Boolean
}