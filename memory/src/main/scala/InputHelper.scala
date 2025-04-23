object InputHelper {

  /**
   * Holt eine gültige Kartenauswahl vom Benutzer.
   * @param prompt Eingabeaufforderung
   * @param game Aktueller Spielzustand
   * @param readLineFunc Optional: Funktion zum Einlesen (für Tests)
   * @return Ein gültiger Index
   */
  def getInput(prompt: String, game: Game, readLineFunc: () => String = () => scala.io.StdIn.readLine()): Int = {
    var index = -1
    var valid = false

    while (!valid) {
      print(prompt)
      try {
        index = readLineFunc().toInt

        val inBounds = index >= 0 && index < game.board.cards.length
        val alreadyRevealed = game.board.cards(index).isRevealed
        val alreadySelected = game.selectedIndices.contains(index)

        if (inBounds && !alreadyRevealed && !alreadySelected) {
          valid = true
        } else {
          println("Ungültiger Index oder Karte bereits gewählt. Bitte erneut versuchen.")
        }
      } catch {
        case _: NumberFormatException =>
          println("Bitte eine gültige Zahl eingeben.")
      }
    }

    index
  }
}
