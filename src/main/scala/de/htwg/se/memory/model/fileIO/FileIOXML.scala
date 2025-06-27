package de.htwg.se.memory.model.fileIO.xml

import de.htwg.se.memory.model.fileIO.FileIOInterface
import de.htwg.se.memory.model._

import scala.xml._
import java.io._

class FileIOXML extends FileIOInterface {

  override def save(game: ModelInterface): Unit = {
    val pw = new PrintWriter(new File("game.xml"))
    pw.write(toXml(game).toString())
    pw.close()
  }

  override def load(): ModelInterface = {
    val file = XML.loadFile("game.xml")

    val players = (file \ "players" \ "player").map { playerNode =>
      val name = (playerNode \ "name").text
      val score = (playerNode \ "score").text.toIntOption.getOrElse(0)
      Player(name, score)
    }.toList

    val cards = (file \ "cards" \ "card").map { cardNode =>
      val value = (cardNode \ "value").text
      val isRevealed = (cardNode \ "isRevealed").text.toBoolean
      Card(value, isRevealed)
    }.toList

    val currentPlayerIndex = (file \ "currentPlayerIndex").text.toIntOption.getOrElse(0)
    val selectedIndices = (file \ "selectedIndices" \ "index").map(_.text.toInt).toList

    val board = Board(cards)
    Game(board, players, currentPlayerIndex, selectedIndices)
  }

  def toXml(game: ModelInterface): Elem = {
    <game>
      <players>
        {game.players.map(player =>
        <player>
          <name>{player.name}</name>
          <score>{player.score}</score>
        </player>
      )}
      </players>
      <cards>
        {game.board.cards.map(card =>
        <card>
          <value>{card.value}</value>
          <isRevealed>{card.isRevealed}</isRevealed>
        </card>
      )}
      </cards>
      <currentPlayerIndex>{game.currentPlayerIndex}</currentPlayerIndex>
      <selectedIndices>
        {game.selectedIndices.map(index => <index>{index}</index>)}
      </selectedIndices>
    </game>
  }
}
