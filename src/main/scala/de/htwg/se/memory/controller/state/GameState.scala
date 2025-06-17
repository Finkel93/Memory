package de.htwg.se.memory.controller.state

import de.htwg.se.memory.controller.Controller
import scala.util.{Try, Failure}

// Gemeinsames Interface für alle Spielzustände
trait GameState {
  def handleInput(input: Int, controller: Controller): Unit
  def name: String
}
