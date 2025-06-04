package de.htwg.se.memory.controller.components

import de.htwg.se.memory.model.{Game, Player}

// Kohäsive Game-State Komponente
trait GameStateManager extends EventPublisher {
  def updateGameState(gameState: Game): Unit
  def getGameState: Game
  def selectCard(index: Int): Game
  def nextTurn(): Game
  def isPairSelected: Boolean
  def isGameOver: Boolean
  def currentPlayer: Player
  def getWinners: List[Player]
}