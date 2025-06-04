package de.htwg.se.memory.controller.components

// Event-System für lose Kopplung
trait ComponentEvent
case object StateChangedEvent extends ComponentEvent
case object GameUpdatedEvent extends ComponentEvent
case object TimerCompletedEvent extends ComponentEvent
