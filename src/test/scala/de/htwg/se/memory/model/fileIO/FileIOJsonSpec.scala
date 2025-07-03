package de.htwg.se.memory.model.fileIO.json

import de.htwg.se.memory.model._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import java.io.File

class FileIOJsonSpec extends AnyWordSpec with Matchers {

  val testFileName = "game.json"
  val fileIO = new FileIOJson

  // Hilfsmethode: löscht die Datei nach jedem Test
  def deleteTestFile(): Unit = {
    val file = new File(testFileName)
    if (file.exists()) file.delete()
  }

  "FileIOJson" should {

    "save game state to file and load it correctly" in {
      val cards = List(Card("A", isRevealed = false), Card("B", isRevealed = true))
      val players = List(Player("Alice", 1), Player("Bob", 2))
      val board = Board(cards)
      val game = Game(board, players, currentPlayerIndex = 1, selectedIndices = List(0, 1))

      // Save game
      fileIO.save(game)

      val loadedGame = fileIO.load()

      loadedGame.players.map(_.name) should contain theSameElementsAs players.map(_.name)
      loadedGame.players.map(_.score) should contain theSameElementsAs players.map(_.score)

      loadedGame.board.cards.map(_.value) should contain theSameElementsAs cards.map(_.value)
      loadedGame.board.cards.map(_.isRevealed) should contain theSameElementsAs cards.map(_.isRevealed)

      loadedGame.currentPlayerIndex shouldBe game.currentPlayerIndex
      loadedGame.selectedIndices shouldBe game.selectedIndices

      deleteTestFile()
    }

    /*"throw an exception when loading from a non-existent file" in {
      deleteTestFile() // sicherstellen, dass Datei nicht existiert
      assertThrows[java.io.FileNotFoundException] {
        fileIO.load()
      }
    }*/

    "set selectedIndices to empty list when JSON field is missing" in {
      val jsonWithoutSelectedIndices = Json.parse(
        """
          |{
          |  "players": [{"name": "Alice", "score": 1}],
          |  "cards": [{"value": "A", "isRevealed": false}],
          |  "currentPlayerIndex": 0
          |}
          |""".stripMargin)

      val fileIO = new FileIOJson
      val game = fileIO.fromJson(jsonWithoutSelectedIndices)

      game.selectedIndices shouldBe List.empty
    }
  }
}
