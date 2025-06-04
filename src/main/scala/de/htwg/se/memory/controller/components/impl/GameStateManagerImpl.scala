package de.htwg.se.memory.controller.components.impl

import de.htwg.se.memory.controller.components._
import de.htwg.se.memory.model.{Game, Player}

// Kohäsive Game-State Management
class GameStateManagerImpl(private var gameState: Game) extends BaseEventPublisher with GameStateManager {

  override def updateGameState(newGameState: Game): Unit = {
    gameState = newGameState
    publish(GameUpdatedEvent)
  }

  override def getGameState: Game = gameState

  override def selectCard(index: Int): Game = {
    if (gameState.board.cards(index).isRevealed) {
      throw new IllegalArgumentException("Karte bereits aufgedeckt")
    }
    val newState = gameState.selectCard(index)
    updateGameState(newState)
    newState
  }

  override def nextTurn(): Game = {
    if (gameState.selectedIndices.size == 2) {
      val idx1 = gameState.selectedIndices(0)
      val idx2 = gameState.selectedIndices(1)
      val card1 = gameState.board.cards(idx1)
      val card2 = gameState.board.cards(idx2)

      val newState = if (card1.value != card2.value) {
        val updatedBoard = gameState.board.hideCard(idx1).hideCard(idx2)
        val nextPlayerIndex = (gameState.currentPlayerIndex + 1) % gameState.players.size

        gameState.copy(
          board = updatedBoard,
          currentPlayerIndex = nextPlayerIndex,
          selectedIndices = List()
        )
      } else {
        // Match found - keep cards revealed, don't change player
        //matchStrategy.handleMatch(controller, idx1, idx2)
        val updatedPlayer = gameState.players(gameState.currentPlayerIndex).addPoint()
        val updatedPlayers = gameState.players.updated(gameState.currentPlayerIndex, updatedPlayer)

        gameState.copy(
          players = updatedPlayers,
          selectedIndices = List()
        )
      }

      updateGameState(newState)
      newState
    } else {
      gameState
    }
  }

  override def isPairSelected: Boolean = gameState.selectedIndices.size == 2
  override def isGameOver: Boolean = gameState.isGameOver
  override def currentPlayer: Player = gameState.players(gameState.currentPlayerIndex)
  override def getWinners: List[Player] = gameState.getWinners
}