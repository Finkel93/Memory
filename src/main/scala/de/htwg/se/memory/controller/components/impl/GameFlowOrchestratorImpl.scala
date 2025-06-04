package de.htwg.se.memory.controller.components.impl

import de.htwg.se.memory.controller.components._
import de.htwg.se.memory.controller.Controller
import de.htwg.se.memory.controller.command._

// High-Level Orchestrator (verwendet andere Components)
class GameFlowOrchestratorImpl(
                                private val gameStateManager: GameStateManager,
                                private val timerManager: TimerManager,
                                private val commandManager: CommandManager,
                                private val controller: Controller
                              ) extends GameFlowOrchestrator {

  private val CARD_DISPLAY_DURATION = 2000

  override def handleCardSelection(index: Int): Unit = {
    // 1. Stop running timer if exists
    if (timerManager.isRunning) {
      timerManager.stopTimer()
      gameStateManager.nextTurn()
    }

    // 2. Handle existing pair
    if (gameStateManager.isPairSelected) {
      val gameState = gameStateManager.getGameState
      val idx1 = gameState.selectedIndices(0)
      val idx2 = gameState.selectedIndices(1)
      val card1 = gameState.board.cards(idx1)
      val card2 = gameState.board.cards(idx2)

      if (card1.value != card2.value) {
        gameStateManager.nextTurn()
      }
    }

    // 3. Select new card
    // NOTE: Hier müsste SetCardCommand refactored werden um mit GameStateManager zu arbeiten
    commandManager.executeCommand(new SetCardCommand(index, controller))

    //gameStateManager.selectCard(index)

    // 4. Process result
    processMatchResult()
  }

  override def processMatchResult(): Unit = {
    if (gameStateManager.isPairSelected) {
      val gameState = gameStateManager.getGameState
      val idx1 = gameState.selectedIndices(0)
      val idx2 = gameState.selectedIndices(1)
      val card1 = gameState.board.cards(idx1)
      val card2 = gameState.board.cards(idx2)

      if (card1.value != card2.value) {
        // Start timer for wrong match
        timerManager.startTimer(CARD_DISPLAY_DURATION, () => {
          gameStateManager.nextTurn()
        })
      } else {
        // Correct match - handle immediately
        gameStateManager.nextTurn()
      }
    }
  }

  override def onEvent(event: ComponentEvent): Unit = {
    event match {
      case GameUpdatedEvent =>
      // React to game state changes if needed
      case StateChangedEvent =>
      // React to command state changes if needed
      case _ =>
      // Handle other events
    }
  }
}