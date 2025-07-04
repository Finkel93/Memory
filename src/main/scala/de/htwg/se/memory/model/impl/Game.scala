package de.htwg.se.memory.model

case class Game(board: Board, players: List[PlayerInterface], currentPlayerIndex: Int = 0, selectedIndices: List[Int] = List()) extends GameInterface {


  def selectCard(index: Int) : Game = {
    if (selectedIndices.contains(index) || board.cards(index).isRevealed) {
      throw new IllegalArgumentException();
    } else {
      val updatedBoard = board.revealCard(index)
      copy(board = updatedBoard, selectedIndices = selectedIndices :+ index)
    }
  }

  def isGameOver: Boolean = {
    // Überprüfen, ob alle Karten revealed sind
    board.cards.forall(_.isRevealed)
  }

  def getWinners: List[PlayerInterface] = {
    if (!isGameOver) return List.empty  // Wenn das Spiel noch nicht vorbei ist, gibt es keine Gewinner

    // Finde die maximale Punktzahl
    val maxScore = players.map(_.score).max

    // Filtere alle Spieler, die diese maximale Punktzahl haben
    players.filter(_.score == maxScore)
  }

  def printResult(winners: List[PlayerInterface]) : String = {
    if (winners.size == 1) {
      s"Der Gewinner ist: ${winners.head.name}"
    }
    else
      "Unentschieden!"
  }
  def update(
              board: BoardInterface = this.board,
              players: List[PlayerInterface] = this.players,
              currentPlayerIndex: Int = this.currentPlayerIndex,
              selectedIndices: List[Int] = this.selectedIndices
            ): GameInterface = {
    copy(
      board = board.asInstanceOf[Board],
      players = players.map(_.asInstanceOf[Player]),
      currentPlayerIndex = currentPlayerIndex,
      selectedIndices = selectedIndices
    )
  }

}