package de.htwg.se.memory.controller

import de.htwg.se.memory.util.Observable
import de.htwg.se.memory.model.{Game, Player}
import de.htwg.se.memory.controller.strategy._
import de.htwg.se.memory.controller.state._
import de.htwg.se.memory.controller.command._
import de.htwg.se.memory.controller.components._
import de.htwg.se.memory.controller.components.impl._

// Facade Pattern - Vereinfacht die Nutzung der Components
class Controller(initialGameState: Game) extends Observable with EventListener {

  // Legacy fields for backward compatibility
  var matchStrategy: MatchStrategy = new KeepOpenStrategy
  var state: GameState = new WaitingFirstCardState

  // Layered Components - Components inside Components
  private val timerManager: TimerManager = new TimerManagerImpl()
  private val commandManager: CommandManager = new CommandManagerImpl()
  private val gameStateManager: GameStateManager = new GameStateManagerImpl(initialGameState)
  private val gameFlowOrchestrator: GameFlowOrchestrator =
    new GameFlowOrchestratorImpl(gameStateManager, timerManager, commandManager, this)

  // Setup event subscriptions for loose coupling
  commandManager.subscribe(this)
  gameStateManager.subscribe(this)

  // Event handling
  override def onEvent(event: ComponentEvent): Unit = {
    event match {
      case StateChangedEvent | GameUpdatedEvent =>
        notifyObservers
      case _ =>
    }
  }

  // Public API - bleibt unverändert für Kompatibilität
  def executeCommand(command: Command): Unit = {
    commandManager.executeCommand(command)
    notifyObservers
  }

  def undo(): Unit = {
    commandManager.undo()
  }

  def redo(): Unit = {
    commandManager.redo()
  }

  def canUndo: Boolean = commandManager.canUndo
  def canRedo: Boolean = commandManager.canRedo

  def setState(newState: GameState): Unit = {
    state = newState
    notifyObservers
  }

  def getStateName: String = state.name

  def handleInput(input: Int): Unit = {
    gameFlowOrchestrator.handleCardSelection(input)
  }

  def selectCard(index: Int): Unit = {
    gameStateManager.selectCard(index)
  }

  def nextTurn(): Unit = {
    gameStateManager.nextTurn()
  }

  // Getters - delegieren an Components
  def isPairSelected: Boolean = gameStateManager.isPairSelected
  def isGameOver: Boolean = gameStateManager.isGameOver
  def currentPlayer: Player = gameStateManager.currentPlayer
  def boardView: String = {
    val gameState = gameStateManager.getGameState
    gameState.board.displayCards(gameState.board.cards)
  }
  def getWinners: List[Player] = gameStateManager.getWinners

  // Getter für aktuellen GameState
  def gameState: Game = gameStateManager.getGameState

  def updateGameState(newState: Game): Unit = {
    gameStateManager.updateGameState(newState)
    notifyObservers
    // notifyObservers is already called by the event system
  }
}
