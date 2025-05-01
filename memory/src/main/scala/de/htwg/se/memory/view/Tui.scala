package de.htwg.se.memory.view

import de.htwg.se.memory.util.Observer
import de.htwg.se.memory.controller.Controller
import de.htwg.se.memory.model.Game

class Tui(val controller: Controller) extends Observer {
  controller.add(this)

  def run(): Unit = {
//    clearConsole()
    println("Willkommen beim Memory-Spiel!")
    println("\n" + controller.boardView)
    gameLoop()
    println("Spiel beendet.")
    val winners = controller.getWinners
    println(controller.game.printResult(winners))
  }

  private def gameLoop(): Unit = {
    if (!controller.isGameOver) {
      displayGame()
      handleInput()
      gameLoop()
    }
  }

  private def clearConsole(): Unit = {
    // ANSI Escape Code zum Löschen des Bildschirms
    print("\u001b[H\u001b[2J")
    System.out.flush()
  }

  private def displayGame(): Unit = {
//    clearConsole()
//    println("\n" + controller.boardView)
    println(s"Spieler am Zug: ${controller.currentPlayer.name}")
  }

  private def handleInput(): Unit = {
    if (controller.game.selectedIndices.isEmpty) {
      val index1 = InputHelper.getInput("Erste Karte wählen: ", controller.game, () => scala.io.StdIn.readLine())
      controller.selectCard(index1)
    } else if (controller.game.selectedIndices.size == 1) {
      val index2 = InputHelper.getInput("Zweite Karte wählen: ", controller.game, () => scala.io.StdIn.readLine())
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
    def readValidInput(): Int = {
      print(prompt)
      try {
        val input = readLineFunc().toInt
        if (isValidInput(input, game)) input
        else {
          println("Ungültiger Index oder Karte bereits gewählt. Bitte erneut versuchen.")
          readValidInput()
        }
      } catch {
        case _: NumberFormatException =>
          println("Bitte eine gültige Zahl eingeben.")
          readValidInput()
        case _: IndexOutOfBoundsException =>
          println("Der Index ist außerhalb des gültigen Bereichs.")
          readValidInput()
      }
    }

    readValidInput()
  }

  private def isValidInput(index: Int, game: Game): Boolean = {
    val inBounds = index >= 0 && index < game.board.cards.length
    val notRevealed = !game.board.cards(index).isRevealed
    val notSelected = !game.selectedIndices.contains(index)
    inBounds && notRevealed && notSelected
  }
}