package de.htwg.se.memory.controller.components

trait EventPublisher {
  def subscribe(listener: EventListener): Unit
  def unsubscribe(listener: EventListener): Unit
  def publish(event: ComponentEvent): Unit
}