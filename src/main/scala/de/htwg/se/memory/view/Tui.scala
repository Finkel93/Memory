package de.htwg.se.memory.view

import de.htwg.se.memory.util.Observer
import de.htwg.se.memory.controller.Controller
import de.htwg.se.memory.model.Game
import scala.util.{Try, Failure, Success}


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
    if (!controller.isGameOver) {
      val inputStr = scala.io.StdIn.readLine("Karte wählen (oder 'u' für Undo, 'r' für Redo): ")

      inputStr match {
        case "u" => controller.undo()
        case "r" => controller.redo()
        case _ =>
          val input = InputHelper.parseInput(inputStr, controller.gameState)
          controller.handleInput(input)
      }
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

      Try(readLineFunc().toInt) match {
        case Success(input) =>
          if (isValidInput(input.toInt, game)) input.toInt
          else {
            println("Ungültiger Index oder Karte bereits gewählt. Bitte erneut versuchen.")
            readValidInput()
          }

        case Failure(_: NumberFormatException) =>
          if (controle) {
            println("Bitte eine gültige Zahl eingeben.")
          }
          readValidInput(true)

        case Failure(e) =>
          println("Unbekannter Fehler: " + e.getMessage)
          readValidInput(true)
      }
    }

    readValidInput()
  }

    def parseInput(inputStr: String, game: Game): Int = {
      val inputTry = Try(inputStr.toInt)
      inputTry match {
        case Success(index) if isValidInput(index, game) => index
        case _ =>
          println("Ungültige Eingabe. Bitte erneut versuchen.")
          parseInput(scala.io.StdIn.readLine(), game)
      }
    }




  def isValidInput(index: Int, game: Game): Boolean = {
    index >= 0 &&
      index < game.board.cards.size &&
      !game.board.cards(index).isRevealed &&
      !game.selectedIndices.contains(index)
  }
}
