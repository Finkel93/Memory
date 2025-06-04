package de.htwg.se.memory.controller.components

// Orchestrator für Game-Flow (verwendet andere Components)
trait GameFlowOrchestrator extends EventListener {
  def handleCardSelection(index: Int): Unit
  def processMatchResult(): Unit
}