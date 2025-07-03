import de.htwg.se.memory.model._
import de.htwg.se.memory.model.fileIO.xml.FileIOXML
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import java.io.File

class FileIOXMLSpec extends AnyWordSpec with Matchers {

  "FileIOXML" should {

    "save and load a game correctly from file" in {
      val cards = List(Card("A", false), Card("B", true))
      val players = List(Player("Alice", 2), Player("Bob", 3))
      val board = Board(cards)
      val game = Game(board, players, currentPlayerIndex = 1, selectedIndices = List(0, 1))

      val fileIO = new FileIOXML

      // Save to file (game.xml in project root)
      fileIO.save(game)

      // Load back from file
      val loadedGame = fileIO.load()

      // Prüfe, ob geladene Daten gleich sind
      loadedGame.players.map(_.name) should contain allElementsOf List("Alice", "Bob")
      loadedGame.players.map(_.score) should contain allElementsOf List(2, 3)

      loadedGame.board.cards.map(_.value) should contain allElementsOf List("A", "B")
      loadedGame.board.cards.map(_.isRevealed) should contain allElementsOf List(false, true)

      loadedGame.currentPlayerIndex shouldBe 1
      loadedGame.selectedIndices should contain allElementsOf List(0, 1)

      // Optional: lösche die Datei danach
      val f = new File("game.xml")
      if (f.exists()) f.delete()
    }

    "handle missing selectedIndices element gracefully" in {
      // Schreibe eine XML-Datei ohne <selectedIndices>
      val xmlWithoutSelectedIndices =
        """<game>
          |  <players>
          |    <player><name>Alice</name><score>2</score></player>
          |  </players>
          |  <cards>
          |    <card><value>A</value><isRevealed>false</isRevealed></card>
          |  </cards>
          |  <currentPlayerIndex>0</currentPlayerIndex>
          |</game>""".stripMargin

      // Schreibe in Datei "game.xml"
      val pw = new java.io.PrintWriter("game.xml")
      pw.write(xmlWithoutSelectedIndices)
      pw.close()

      val fileIO = new FileIOXML
      val loadedGame = fileIO.load()

      loadedGame.selectedIndices shouldBe empty

      // Datei löschen
      val f = new File("game.xml")
      if (f.exists()) f.delete()
    }
  }
}
