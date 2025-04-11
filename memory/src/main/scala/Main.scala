// === Player ===

case class Player(name: String, score: Int = 0) {
  def addPoint(): Player = copy(score = score + 1)
}
// === Card ===
case class Card(value: String, isRevealed: Boolean = false) {
  def reveal(): Card = copy(isRevealed = true)
  def hide(): Card = copy(isRevealed = false)
}
// === Board ===
case class Board(cards: List[Card]) {
  def revealCard(index: Int): Board = {
    if (cards(index).isRevealed) {
      throw new IllegalArgumentException();
    }
    val updatedCard = cards(index).reveal()
    copy(cards = cards.updated(index, updatedCard))
  }

  def hideCard(index: Int): Board = {
    if (!cards(index).isRevealed) {
      throw new IllegalArgumentException();
    }
    val updatedCard = cards(index).hide()
    copy(cards = cards.updated(index, updatedCard))
  }

  def isGameOver: Boolean = cards.forall(_.isRevealed)


  def displayCards(cards: List[Card]): String = {
    val sb = new StringBuilder()
    for (i <- cards.indices) {
      val card = cards(i)
      val display = if (card.isRevealed) s" ${card.value} " else " ? "
      sb.append(f"[$i%2d:$display]")

      // Add line break every 4 cards for better readability
      //if ((i + 1) % 4 == 0) sb.append("\n")
    }
    sb.toString()
  }
}

case class Game(board: Board, players: List[Player], currentPlayerIndex: Int = 0, selectedIndices: List[Int] = List()) {


  def selectCard(index: Int): Game = {
    if (selectedIndices.contains(index) || board.cards(index).isRevealed) {
      throw new IllegalArgumentException();
      this
    } else {
      val updatedBoard = board.revealCard(index)
      copy(board = updatedBoard, selectedIndices = selectedIndices :+ index)
    }
  }



  //def isGameOver: Boolean = board.isGameOver

  def isGameOver: Boolean = {
    // Überprüfen, ob alle Karten revealed sind
    board.cards.forall(_.isRevealed)
  }

  def getWinners: List[Player] = {
    if (!isGameOver) return List.empty  // Wenn das Spiel noch nicht vorbei ist, gibt es keine Gewinner

    // Finde die maximale Punktzahl
    val maxScore = players.map(_.score).max

    // Filtere alle Spieler, die diese maximale Punktzahl haben
    players.filter(_.score == maxScore)
  }

}

