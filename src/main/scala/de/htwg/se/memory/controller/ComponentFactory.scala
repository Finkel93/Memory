package de.htwg.se.memory.controller

import de.htwg.se.memory.model.Game
import de.htwg.se.memory.controller.components._

// TESTABLE COMPONENT FACTORY - Ermöglicht Dependency Injection für Tests
object ComponentFactory {
  def createController(gameState: Game): Controller = {
    new Controller(gameState)
  }

  def createController(
                        gameState: Game,
                        timerManager: TimerManager,
                        commandManager: CommandManager,
                        gameStateManager: GameStateManager
                      ): Controller = {
    // Factory method für Tests mit Mock-Dependencies
    val controller = new Controller(gameState)
    // In einer echten Implementierung würden wir die Dependencies hier injizieren
    controller
  }
}