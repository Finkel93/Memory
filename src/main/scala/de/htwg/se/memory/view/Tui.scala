package de.htwg.se.memory.view

import de.htwg.se.memory.util.Observer
import de.htwg.se.memory.controller.Controller
import de.htwg.se.memory.model.Game

class Tui(val controller: Controller) extends Observer {
  controller.add(this)

  def run(): Unit = {
    println("Willkommen beim Memory-Spiel!")
    println("\n" + controller.boardView)
    gameLoop()
    println("Spiel beendet.")
    val winners = controller.getWinners
    println(controller.gameState.printResult(winners))
  }

  private def gameLoop(): Unit = {
    if (!controller.isGameOver) {
      displayGame()
      handleInput()
      gameLoop()
    }
  }

  def displayGame(): Unit = {

    println(s"Spieler am Zug: ${controller.currentPlayer.name}")
  }

  def handleInput(): Unit = {
    if (controller.gameState.selectedIndices.isEmpty) {
      val index1 = InputHelper.getInput("Erste Karte wählen: ", controller.gameState, () => scala.io.StdIn.readLine())
      println("index1")
      controller.selectCard(index1)
    } else if (controller.gameState.selectedIndices.size == 1) {
      val index2 = InputHelper.getInput("Zweite Karte wählen: ", controller.gameState, () => scala.io.StdIn.readLine())
      controller.selectCard(index2)
      controller.nextTurn()
    }
  }

  override def update: Unit = {
//    clearConsole()
    println("\nupdated board: " + controller.boardView)
  }
}

object InputHelper {
  def getInput(prompt: String, game: Game, readLineFunc: () => String): Int = {
    def readValidInput(controle: Boolean = false): Int = {
      if (!controle) {
        print(prompt)
      }
      try {
        val input = readLineFunc().toInt
        if (isValidInput(input, game)) input
        else {
          println("Ungültiger Index oder Karte bereits gewählt. Bitte erneut versuchen.")
          readValidInput()
        }
      } catch {
        case e: NumberFormatException =>
          if (controle) {
            println("Bitte eine gültige Zahl eingeben.")
          }
          readValidInput(true)
      }
    }

    readValidInput()
  }

  def isValidInput(index: Int, game: Game): Boolean = {
    val inBounds = index >= 0 && index < game.board.cards.length
    if(index >=  game.board.cards.length) {
      return false
    }
    val notRevealed = !game.board.cards(index).isRevealed
    val notSelected = !game.selectedIndices.contains(index)
    inBounds && notRevealed && notSelected
  }
}