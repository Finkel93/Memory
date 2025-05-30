package de.htwg.se.memory.controller.command

import de.htwg.se.memory.controller.Controller

class SetCardCommand(index: Int, controller: Controller) extends Command {
  private var previousState = controller.gameState
  private var newState = controller.gameState

  override def doStep(): Unit = {
    previousState = controller.gameState
    controller.selectCard(index)
    newState = controller.gameState
  }

  override def undoStep(): Unit = {
    controller.gameState = previousState
    controller.notifyObservers
  }

  override def redoStep(): Unit = {
    controller.gameState = newState
    controller.notifyObservers
  }
}
