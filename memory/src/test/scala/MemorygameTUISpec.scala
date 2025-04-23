import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import java.io._

class MemoryGameTUISpec extends AnyWordSpec with Matchers {

  "The MemoryGameTUI" should {

    "run through a game with simulated input" in {
      // Simulierte Eingaben, die der Benutzer tätigen würde
      val simulatedInput = List("0", "1", "2", "3", "4", "5", "6", "7").mkString("\n")
      val inputStream = new ByteArrayInputStream(simulatedInput.getBytes)
      System.setIn(inputStream)

      // Fangen der Konsolenausgabe
      val outputStream = new ByteArrayOutputStream()
      val printStream = new PrintStream(outputStream)
      val originalOut = System.out
      System.setOut(printStream)

      try {
        // Starte die TUI
        MemorygameTUI.main(Array())
      } finally {
        // Wiederherstellen der System.out und System.in
        System.setIn(System.in)
        System.setOut(originalOut)
      }

      // Den Test daraufhin prüfen, ob das Spiel wie erwartet läuft

      val output = outputStream.toString

      // Überprüfen, ob der Text aus der TUI-Ausgabe vorhanden ist
      output should include ("Willkommen beim Memory-Spiel")
      output should include regex "Spiel beendet".r
      output should include regex "(Der Gewinner ist|Unentschieden)".r

      // Optional: Überprüfen, dass die Eingaben und Kartennummern korrekt verarbeitet wurden
      output should include("Aktualisiertes Board:")
    }
  }
}
