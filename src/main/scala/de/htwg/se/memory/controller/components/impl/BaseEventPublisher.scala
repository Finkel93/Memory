package de.htwg.se.memory.controller.components.impl

import de.htwg.se.memory.controller.components._
import scala.collection.mutable

// Base-Klasse für Event-Publishing (DRY-Prinzip)
abstract class BaseEventPublisher extends EventPublisher {
  private val listeners: mutable.Set[EventListener] = mutable.Set()

  override def subscribe(listener: EventListener): Unit = {
    listeners += listener
  }

  override def unsubscribe(listener: EventListener): Unit = {
    listeners -= listener
  }

  override def publish(event: ComponentEvent): Unit = {
    listeners.foreach(_.onEvent(event))
  }
}