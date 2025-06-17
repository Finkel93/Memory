package de.htwg.se.memory.controller.state

import de.htwg.se.memory.controller.Controller
import scala.util.{Try, Failure}

class WaitingFirstCardState extends GameState {
  var output = ""
  override def handleInput(input: Int, controller: Controller): Unit = {
    Try {
      controller.selectCard(input)
    }.map { _ =>
      controller.setState(new WaitingSecondCardState)
    }.recover {
      case e: IllegalArgumentException =>
        println("Ungültige Auswahl: " + e.getMessage)
    }
  }


  override def name: String = "WaitingFirstCard"
}