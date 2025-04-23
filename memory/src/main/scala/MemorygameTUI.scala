/*object MemorygameTUI {
  def main(args: Array[String]): Unit = {
    println("Willkommen beim Memory-Spiel!")

    val values = List("A", "A", "B", "B", "C", "C", "D", "D")
    val cards = scala.util.Random.shuffle(values).map(Card(_))
    val board = Board(cards)

    val players = List(Player("Spieler 1"), Player("Spieler 2"))
    val game = Game(board, players)

    val controller = new GameController(game)

    while (!controller.isGameOver) {
      println("\n" + controller.boardView)
      println(s"Spieler am Zug: ${controller.currentPlayer.name}")

      // Erste Karte auswählen
      val index1 = InputHelper.getInput("Erste Karte wählen: ", controller.game)
      controller.selectCard(index1)

      // Nach der ersten Karte das Board anzeigen
      println("\nAktualisiertes Board:")
      println(controller.boardView)

      // Zweite Karte auswählen
      val index2 = InputHelper.getInput("Zweite Karte wählen: ", controller.game)
      controller.selectCard(index2)

      // Nach der Auswahl der zweiten Karte das Board wieder anzeigen
      println("\nAktualisiertes Board:")
      println(controller.boardView)

      // Nächster Zug
      controller.nextTurn()
    }

    println("Spiel beendet.")
    val winners = controller.getWinners
    if (winners.size == 1)
      println(s"Der Gewinner ist: ${winners.head.name}")
    else
      println("Unentschieden!")
  }
}

 */
