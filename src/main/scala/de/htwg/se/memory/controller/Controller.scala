package de.htwg.se.memory.controller

import de.htwg.se.memory.util.Observable
import de.htwg.se.memory.model.ModelInterface
import de.htwg.se.memory.model.PlayerInterface
import de.htwg.se.memory.controller.strategy._
import de.htwg.se.memory.controller.state._
import de.htwg.se.memory.controller.command._
import scala.collection.mutable
import javax.swing.Timer
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import de.htwg.se.memory.util.Observer
import com.google.inject.Inject
import de.htwg.se.memory.model.fileIO.FileIOInterface

class Controller @Inject()(var gameState: ModelInterface, fileIO: FileIOInterface)
  extends ControllerInterface with Observable {

  var matchStrategy: MatchStrategy = new KeepOpenStrategy
  var state: GameState = new WaitingFirstCardState

  private val undoStack: mutable.Stack[Command] = mutable.Stack()
  private val redoStack: mutable.Stack[Command] = mutable.Stack()

  var hideCardsTimer: Option[Timer] = None
  private val CARD_DISPLAY_DURATION = 2000 // 2 Sekunden

  def saveGame(): Unit = {
    fileIO.save(gameState.asInstanceOf[ModelInterface])  // Typumwandlung ggf. erforderlich
  }

  def loadGame(): Unit = {
    gameState = fileIO.load()
    notifyObservers
  }



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
    if (hideCardsTimer.exists(_.isRunning)) {
      hideCardsTimer.foreach(_.stop())
      hideCardsTimer = None
      nextTurn()
    }

    if (gameState.selectedIndices.size == 2) {
      val idx1 = gameState.selectedIndices(0)
      val idx2 = gameState.selectedIndices(1)
      val card1 = gameState.board.cards(idx1)
      val card2 = gameState.board.cards(idx2)

      if (card1.value != card2.value) {
        nextTurn()
      }
    }

    val cmd = new SetCardCommand(input, this)
    executeCommand(cmd)

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

  def startHideCardsTimer(): Unit = {
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

        gameState = gameState.update(
          board = updatedBoard,
          currentPlayerIndex = nextPlayerIndex,
          selectedIndices = List()
        )
      }
      notifyObservers
    }
  }

  def isGameOver: Boolean = gameState.isGameOver
  def currentPlayer: PlayerInterface = gameState.players(gameState.currentPlayerIndex)
  def boardView: String = gameState.board.displayCards(gameState.board.cards)
  def getWinners: List[PlayerInterface] = gameState.getWinners

  override def add(observer: Observer): Unit = {
    super[Observable].add(observer)  // Expliziter Aufruf der add-Methode von Observable
  }
  override def notifyObservers: Unit = super.notifyObservers
}
