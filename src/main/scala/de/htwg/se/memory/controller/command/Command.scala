package de.htwg.se.memory.controller.command

trait Command {
  def doStep(): Unit
  def undoStep(): Unit
  def redoStep(): Unit
}
