package de.htwg.se.memory.controller.state

import de.htwg.se.memory.controller.Controller
import scala.util.{Try, Failure}

class WaitingSecondCardState extends GameState {
  override def handleInput(input: Int, controller: Controller): Unit = {
    Try {
      controller.selectCard(input)
      controller.nextTurn()

      if (controller.isGameOver) {
        controller.setState(new GameOverState)
      } else {
        controller.setState(new WaitingFirstCardState)
      }
    }.recover {
      case e: IllegalArgumentException =>
        println("Ungültige Auswahl: " + e.getMessage)
    }
  }

  override def name: String = "WaitingSecondCard"
}