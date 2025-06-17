package de.htwg.se.memory.view

import de.htwg.se.memory.util.Observer
import de.htwg.se.memory.controller.ControllerInterface
import de.htwg.se.memory.model.ModelInterface

import scala.util.{Failure, Success, Try}
import com.google.inject.Inject

class Tui @Inject()(val controller: ControllerInterface) extends Observer {
  controller.add(this)

  def start(): Unit = {
    println("Willkommen beim Memory-Spiel!")
    println("\n" + controller.boardView)
    displayGame()
    println("Karte waehlen (oder 'u' fuer Undo, 'r' fuer Redo): ")
  }

  def displayGame(): Unit = {
    println(s"Spieler am Zug: ${controller.currentPlayer.name}")
  }

  def handleInput(inputStr: String): Unit = {
    inputStr match {
      case "u" => controller.undo()
      case "r" => controller.redo()
      case _ =>
        InputHelper.parseInput(inputStr, controller.gameState) match {
          case Some(index) => controller.handleInput(index)
          case None =>
            println("Ungueltige Eingabe. Bitte erneut versuchen.")
        }
    }
    if (!controller.isGameOver) {
      println("Karte waehlen (oder 'u' fuer Undo, 'r' fuer Redo): ")
    }
  }

  override def update: Unit = {
    println("\nupdated board: " + controller.boardView)
    if (controller.isGameOver) {
      println("Spiel beendet!")
      val winners = controller.getWinners
      println(controller.gameState.printResult(winners))
    } else {
      displayGame()
    }
  }
}

object InputHelper {
  def getInput(prompt: String, game: ModelInterface, readLineFunc: () => String): Int = {
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

  def parseInput(inputStr: String, game: ModelInterface): Option[Int] = {
    Try(inputStr.toInt).toOption
      .filter(index => isValidInput(index, game))
  }

  def isValidInput(index: Int, game: ModelInterface): Boolean = {
    index >= 0 &&
      index < game.board.cards.size &&
      !game.board.cards(index).isRevealed &&
      !game.selectedIndices.contains(index)
  }
}
