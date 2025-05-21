package de.htwg.se.memory.controller

import de.htwg.se.memory.util.Observable
import de.htwg.se.memory.model.{Game, Player}
import de.htwg.se.memory.controller.strategy._
import de.htwg.se.memory.controller.state._
import scala.util.{Try, Failure}
import de.htwg.se.memory.controller.command.SetCardCommand
import de.htwg.se.memory.controller.command.Command
import scala.collection.mutable




class Controller(var gameState: Game) extends Observable {

  var matchStrategy: MatchStrategy = new KeepOpenStrategy // Standardstrategie
  var state: GameState = new WaitingFirstCardState

  private val undoStack: mutable.Stack[Command] = mutable.Stack()
  private val redoStack: mutable.Stack[Command] = mutable.Stack()

  def executeCommand(command: Command): Unit = {
    command.doStep()
    undoStack.push(command)
    redoStack.clear()
  }

  def undo(): Unit = {
    if (undoStack.nonEmpty) {
      val command = undoStack.pop()
      command.undoStep()
      redoStack.push(command)
    }
  }

  def redo(): Unit = {
    if (redoStack.nonEmpty) {
      val command = redoStack.pop()
      command.redoStep()
      undoStack.push(command)
    }
  }

  /*def setMatchStrategy(strategy: MatchStrategy): Unit = {
    matchStrategy = strategy
  }*/

  def setState(newState: GameState): Unit = {
    state = newState
    notifyObservers
  }



  def getStateName: String = state.name

  def handleInput(input: Int): Unit = {
    val cmd = new SetCardCommand(input, this)
    executeCommand(cmd)
  }

  def selectCard(index: Int): Unit = {
    if (gameState.board.cards(index).isRevealed) {
      throw new IllegalArgumentException("Karte bereits aufgedeckt")
    }

    gameState = gameState.selectCard(index)
    notifyObservers
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