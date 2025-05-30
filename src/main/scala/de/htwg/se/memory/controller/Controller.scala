package de.htwg.se.memory.controller

import de.htwg.se.memory.util.Observable
import de.htwg.se.memory.model.{Game, Player}
import de.htwg.se.memory.controller.strategy._
import de.htwg.se.memory.controller.state._
import de.htwg.se.memory.controller.command._
import scala.collection.mutable
import javax.swing.Timer
import java.awt.event.ActionListener

class Controller(var gameState: Game) extends Observable {

  var matchStrategy: MatchStrategy = new KeepOpenStrategy
  var state: GameState = new WaitingFirstCardState

  private val undoStack: mutable.Stack[Command] = mutable.Stack()
  private val redoStack: mutable.Stack[Command] = mutable.Stack()

  // Timer für falsches Pärchen
  private var hideCardsTimer: Option[Timer] = None
  private val CARD_DISPLAY_DURATION = 2000 // 2 Sekunden

  def executeCommand(command: Command): Unit = {
    command.doStep()
    undoStack.push(command)
    redoStack.clear()
    notifyObservers
  }

  def undo(): Unit = {
    if (undoStack.nonEmpty) {
      val command = undoStack.pop()
      command.undoStep()
      redoStack.push(command)
      notifyObservers
    }
  }

  def redo(): Unit = {
    if (redoStack.nonEmpty) {
      val command = redoStack.pop()
      command.redoStep()
      undoStack.push(command)
      notifyObservers
    }
  }



  def canUndo: Boolean = undoStack.nonEmpty
  def canRedo: Boolean = redoStack.nonEmpty

  def setState(newState: GameState): Unit = {
    state = newState
    notifyObservers
  }

  def getStateName: String = state.name

  def handleInput(input: Int): Unit = {
    // 1. Falls Timer läuft, stoppen und turn beenden
    if (hideCardsTimer.exists(_.isRunning)) {
      hideCardsTimer.foreach(_.stop())
      hideCardsTimer = None
      nextTurn()
    }

    // 2. Falls kein Timer, aber zwei ungleiche Karten offen → nextTurn
    if (gameState.selectedIndices.size == 2) {
      val idx1 = gameState.selectedIndices(0)
      val idx2 = gameState.selectedIndices(1)
      val card1 = gameState.board.cards(idx1)
      val card2 = gameState.board.cards(idx2)

      if (card1.value != card2.value) {
        nextTurn()
      }
    }

    // 3. Jetzt kann neue Karte gewählt werden
    val cmd = new SetCardCommand(input, this)
    executeCommand(cmd)

    // 4. Nach dem Zug: prüfen ob 2 Karten aufgedeckt → Timer oder nextTurn
    if (gameState.selectedIndices.size == 2) {
      val idx1 = gameState.selectedIndices(0)
      val idx2 = gameState.selectedIndices(1)
      val card1 = gameState.board.cards(idx1)
      val card2 = gameState.board.cards(idx2)

      if (card1.value != card2.value) {
        startHideCardsTimer()
      } else {
        nextTurn()
      }
    }
  }


  private def startHideCardsTimer(): Unit = {
    hideCardsTimer.foreach(_.stop())
    hideCardsTimer = Some(new Timer(CARD_DISPLAY_DURATION, new ActionListener {
      override def actionPerformed(e: java.awt.event.ActionEvent): Unit = {
        nextTurn()
        hideCardsTimer.foreach(_.stop())
        hideCardsTimer = None
      }
    }))
    hideCardsTimer.foreach { t =>
      t.setRepeats(false)
      t.start()
    }
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
      }
      notifyObservers
    }
  }

  def isPairSelected: Boolean = gameState.selectedIndices.size == 2
  def isGameOver: Boolean = gameState.isGameOver
  def currentPlayer: Player = gameState.players(gameState.currentPlayerIndex)
  def boardView: String = gameState.board.displayCards(gameState.board.cards)
  def getWinners: List[Player] = gameState.getWinners
}
