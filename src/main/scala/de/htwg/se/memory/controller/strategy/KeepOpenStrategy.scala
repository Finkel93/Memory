package de.htwg.se.memory.controller.strategy
import de.htwg.se.memory.controller.Controller

class KeepOpenStrategy extends MatchStrategy {
  override def handleMatch(controller: Controller, idx1: Int, idx2: Int): Unit = {
    val current = controller.gameState.players(controller.gameState.currentPlayerIndex)
    val updatedPlayer = current.addPoint()
    val updatedPlayers = controller.gameState.players.updated(controller.gameState.currentPlayerIndex, updatedPlayer)

    controller.gameState = controller.gameState.update(
      players = updatedPlayers,
      selectedIndices = List()
    )
    controller.notifyObservers
  }
}
