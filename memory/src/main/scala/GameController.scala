class GameController(var game: Game) {

  def selectCard(index: Int): Either[String, Unit] = {
    try {
      game = game.selectCard(index)
      Right(())
    } catch {
      case _: IllegalArgumentException =>
        Left("Ungültige Auswahl. Karte entweder schon aufgedeckt oder doppelt gewählt.")
    }
  }

  def nextTurn(): Unit = {
    if (game.selectedIndices.size == 2) {
      val idx1 = game.selectedIndices(0)
      val idx2 = game.selectedIndices(1)

      val card1 = game.board.cards(idx1)
      val card2 = game.board.cards(idx2)

      if (card1.value == card2.value) {
        // Treffer → Punkt
        val current = game.players(game.currentPlayerIndex)
        val updatedPlayer = current.addPoint()
        val updatedPlayers = game.players.updated(game.currentPlayerIndex, updatedPlayer)
        game = game.copy(players = updatedPlayers, selectedIndices = List())
      } else {
        // Kein Treffer → Karten verstecken, nächster Spieler
        val updatedBoard = game.board.hideCard(idx1).hideCard(idx2)
        val nextPlayerIndex = (game.currentPlayerIndex + 1) % game.players.size
        game = game.copy(board = updatedBoard, currentPlayerIndex = nextPlayerIndex, selectedIndices = List())
      }
    }
  }

  def isGameOver: Boolean = game.isGameOver

  def currentPlayer: Player = game.players(game.currentPlayerIndex)

  def boardView: String = game.board.displayCards(game.board.cards)

  def getWinners: List[Player] = game.getWinners

  def printResult(winner: List[Player]) : String = game.printResult(winner)
}
