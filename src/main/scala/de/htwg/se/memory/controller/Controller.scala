package de.htwg.se.memory.controller

import de.htwg.se.memory.util.Observable
import de.htwg.se.memory.model.{Game, Player}
import de.htwg.se.memory.controller.strategy._
import de.htwg.se.memory.controller.state._




class Controller(var gameState: Game) extends Observable {

  var matchStrategy: MatchStrategy = new KeepOpenStrategy // Standardstrategie
  var state: GameState = new WaitingFirstCardState

  def setMatchStrategy(strategy: MatchStrategy): Unit = {
    matchStrategy = strategy
  }

  def setState(newState: GameState): Unit = {
    this.state = newState
    notifyObservers
  }

  def getStateName: String = state.name

  def handleInput(input: Int): Unit = {
    state.handleInput(input, this)
  }

  def selectCard(index: Int): Unit = {
    try {
      gameState = gameState.selectCard(index)
      notifyObservers
    } catch {
      case _: IllegalArgumentException =>
        throw new IllegalArgumentException("Karte bereits aufgedeckt")
    }
  }

  def nextTurn(): Unit = {
    if (gameState.selectedIndices.size == 2) {
      val idx1 = gameState.selectedIndices(0)
      val idx2 = gameState.selectedIndices(1)

      val card1 = gameState.board.cards(idx1)
      val card2 = gameState.board.cards(idx2)

      if (card1.value == card2.value) {
        matchStrategy.handleMatch(this, idx1, idx2)
      } else {
        val updatedBoard = gameState.board.hideCard(idx1).hideCard(idx2)
        val nextPlayerIndex = (gameState.currentPlayerIndex + 1) % gameState.players.size
        gameState = gameState.copy(
          board = updatedBoard,
          currentPlayerIndex = nextPlayerIndex,
          selectedIndices = List()
        )
        notifyObservers
      }
    }
  }


  def isGameOver: Boolean = gameState.isGameOver
  def currentPlayer: Player = gameState.players(gameState.currentPlayerIndex)
  def boardView: String = gameState.board.displayCards(gameState.board.cards)
  def getWinners: List[Player] = gameState.getWinners
}