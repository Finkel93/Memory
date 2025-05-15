package de.htwg.se.memory.controller.strategy
import de.htwg.se.memory.controller.Controller

class AlwaysHideStrategy extends MatchStrategy {
  override def handleMatch(controller: Controller, idx1: Int, idx2: Int): Unit = {
    val updatedBoard = controller.gameState.board.hideCard(idx1).hideCard(idx2)
    val nextPlayerIndex = (controller.gameState.currentPlayerIndex + 1) % controller.gameState.players.size

    controller.gameState = controller.gameState.copy(
      board = updatedBoard,
      currentPlayerIndex = nextPlayerIndex,
      selectedIndices = List()
    )
    controller.notifyObservers
  }
}
