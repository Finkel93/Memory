package de.htwg.se.memory.controller.state

import de.htwg.se.memory.controller.Controller

// Gemeinsames Interface für alle Spielzustände
trait GameState {
  def handleInput(input: Int, controller: Controller): Unit
  def name: String
}

// Zustand: Erwartet erste Kartenwahl
class WaitingFirstCardState extends GameState {
  override def handleInput(input: Int, controller: Controller): Unit = {
    try {
      controller.selectCard(input)
      controller.setState(new WaitingSecondCardState)
    } catch {
      case e: IllegalArgumentException =>
        println("Ungültige Auswahl: " + e.getMessage)
    }
  }

  override def name: String = "WaitingFirstCard"
}

// Zustand: Erwartet zweite Kartenwahl
class WaitingSecondCardState extends GameState {
  override def handleInput(input: Int, controller: Controller): Unit = {
    try {
      controller.selectCard(input)
      controller.nextTurn()

      if (controller.isGameOver) {
        controller.setState(new GameOverState)
      } else {
        controller.setState(new WaitingFirstCardState)
      }
    } catch {
      case e: IllegalArgumentException =>
        println("Ungültige Auswahl: " + e.getMessage)
    }
  }

  override def name: String = "WaitingSecondCard"
}

// Zustand: Spiel ist vorbei
class GameOverState extends GameState {
  override def handleInput(input: Int, controller: Controller): Unit = {

    println("Spiel ist vorbei. Eingabe wird ignoriert.")
  }

  override def name: String = "GameOver"
}
