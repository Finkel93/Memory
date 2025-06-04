package de.htwg.se.memory.view

import de.htwg.se.memory.util.Observer
import de.htwg.se.memory.controller.Controller
import de.htwg.se.memory.model.Game

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

import scala.swing._

class Tui(val controller: Controller) extends Observer {
  controller.add(this)

  def start(): Unit = {
    println("Willkommen beim Memory-Spiel!")
    println("\n" + controller.boardView)
    displayGame()

    // Eingabeaufforderung anzeigen
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
    // Neue Eingabeaufforderung anzeigen
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

    def parseInput(inputStr: String, game: Game): Option[Int] = {
      Try(inputStr.toInt).toOption
        .filter(index => isValidInput(index, game))
    }

    def isValidInput(index: Int, game: Game): Boolean = {
      index >= 0 &&
        index < game.board.cards.size &&
        !game.board.cards(index).isRevealed &&
        !game.selectedIndices.contains(index)
    }
  }
