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