package de.htwg.se.memory.controller

import de.htwg.se.memory.util.Observable
import de.htwg.se.memory.model.{Game, Player}

class Controller(var gameState: Game) extends Observable {
  def selectCard(index: Int): Unit = {
    try {
      gameState = gameState.selectCard(index)
      notifyObservers
    } catch {
      case _: IllegalArgumentException =>
        throw new IllegalArgumentException("Karte bereits aufgedeckt")
    }
  }

  def nextTurn(): Unit = {
    if (gameState.selectedIndices.size == 2) {
      val idx1 = gameState.selectedIndices(0)
      val idx2 = gameState.selectedIndices(1)

      val card1 = gameState.board.cards(idx1)
      val card2 = gameState.board.cards(idx2)

      if (card1.value == card2.value) {
        // Treffer: Punkt für aktuellen Spieler, Karten bleiben aufgedeckt
        val current = gameState.players(gameState.currentPlayerIndex)
        val updatedPlayer = current.addPoint()
        val updatedPlayers = gameState.players.updated(gameState.currentPlayerIndex, updatedPlayer)
        gameState = gameState.copy(
          players = updatedPlayers,
          selectedIndices = List()  // Auswahl zurücksetzen
        )
        notifyObservers
      } else {
        // Kein Treffer: Karten verdecken und Spieler wechseln
        // Beide Karten wieder verdecken
        val updatedBoard = gameState.board.hideCard(idx1).hideCard(idx2)
        val nextPlayerIndex = (gameState.currentPlayerIndex + 1) % gameState.players.size
        gameState = gameState.copy(
          board = updatedBoard,
          currentPlayerIndex = nextPlayerIndex,
          selectedIndices = List()  // Auswahl zurücksetzen
        )
      }
      notifyObservers
    }
  }

  def isGameOver: Boolean = gameState.isGameOver
  def currentPlayer: Player = gameState.players(gameState.currentPlayerIndex)
  def boardView: String = gameState.board.displayCards(gameState.board.cards)
  def getWinners: List[Player] = gameState.getWinners
}