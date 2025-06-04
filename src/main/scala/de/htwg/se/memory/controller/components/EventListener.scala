package de.htwg.se.memory.controller.components

trait EventListener {
  def onEvent(event: ComponentEvent): Unit
}