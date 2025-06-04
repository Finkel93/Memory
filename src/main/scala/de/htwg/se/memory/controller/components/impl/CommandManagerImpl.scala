package de.htwg.se.memory.controller.components.impl

import de.htwg.se.memory.controller.components._
import de.htwg.se.memory.controller.command.Command
import de.htwg.se.memory.controller.Controller
import scala.collection.mutable

// Kohäsive Command-Management Implementation
class CommandManagerImpl extends BaseEventPublisher with CommandManager {
  private val undoStack: mutable.Stack[Command] = mutable.Stack()
  private val redoStack: mutable.Stack[Command] = mutable.Stack()

  override def executeCommand(command: Command): Unit = {
    command.doStep()
    undoStack.push(command)
    redoStack.clear()
    publish(StateChangedEvent)
  }

  override def undo(): Unit = {
    if (undoStack.nonEmpty) {
      val command = undoStack.pop()
      command.undoStep()
      redoStack.push(command)
      publish(StateChangedEvent)
    }
  }

  override def redo(): Unit = {
    if (redoStack.nonEmpty) {
      val command = redoStack.pop()
      command.redoStep()
      undoStack.push(command)
      publish(StateChangedEvent)
    }
  }

  override def canUndo: Boolean = undoStack.nonEmpty
  override def canRedo: Boolean = redoStack.nonEmpty
}