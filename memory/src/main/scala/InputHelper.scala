object InputHelper {

  /**
   * Holt eine gültige Kartenauswahl vom Benutzer.
   * @param prompt Eingabeaufforderung
   * @param game Aktueller Spielzustand
   * @param readLineFunc Optional: Funktion zum Einlesen (für Tests)
   * @return Ein gültiger Index
   */
  def getInput(prompt: String, game: Game, readLineFunc: () => String): Int = {
    var index = -1
    var valid = false

    while (!valid) {
      print(prompt)
      try {
        index = readLineFunc().toInt

        // Überprüfe, ob der Index im gültigen Bereich liegt
        val inBounds = index >= 0 && index < game.board.cards.length
        val alreadyRevealed = game.board.cards(index).isRevealed
        val alreadySelected = game.selectedIndices.contains(index)

        // Wenn der Index gültig ist, Karte nicht bereits aufgedeckt oder gewählt, dann weiter
        if (inBounds && !alreadyRevealed && !alreadySelected) {
          valid = true
        } else {
          // Fehlerfall, Index außerhalb der Grenzen oder Karte bereits gewählt
          println("Ungültiger Index oder Karte bereits gewählt. Bitte erneut versuchen.")
        }
      } catch {
        case _: NumberFormatException =>
          // Fehlerfall: Eingabe keine Zahl
          println("Bitte eine gültige Zahl eingeben.")
        case _: IndexOutOfBoundsException =>
          // Fehlerfall: Index außerhalb der Liste
          println("Der Index ist außerhalb des gültigen Bereichs.")
      }
    }

    index
  }
}



