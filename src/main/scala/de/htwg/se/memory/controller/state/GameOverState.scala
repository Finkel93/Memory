package de.htwg.se.memory.controller.state

import de.htwg.se.memory.controller.Controller
import scala.util.{Try, Failure}

class GameOverState extends GameState {
  override def handleInput(input: Int, controller: Controller): Unit = {
    println("Spiel ist vorbei. Eingabe wird ignoriert.")
  }

  override def name: String = "GameOver"
}