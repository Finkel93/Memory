package de.htwg.se.memory.model.fileIO.json

import de.htwg.se.memory.model.fileIO.FileIOInterface
import de.htwg.se.memory.model._
import play.api.libs.json._

import java.io._

class FileIOJson extends FileIOInterface {

  override def save(game: ModelInterface): Unit = {
    val pw = new PrintWriter(new File("game.json"))
    pw.write(Json.prettyPrint(toJson(game)))
    pw.close()
  }

  override def load(): ModelInterface = {
    val source = scala.io.Source.fromFile("game.json").getLines().mkString
    val json = Json.parse(source)
    fromJson(json)
  }

  def fromJson(json: JsValue): Game = {
    val playersJson = (json \ "players").as[List[JsValue]]
    val players = playersJson.map { p =>
      Player(
        (p \ "name").as[String],
        (p \ "score").asOpt[Int].getOrElse(0)
      )
    }

    val cardsJson = (json \ "cards").as[List[JsValue]]
    val cards = cardsJson.map { c =>
      Card((c \ "value").as[String], (c \ "isRevealed").as[Boolean])
    }

    val currentPlayerIndex = (json \ "currentPlayerIndex").asOpt[Int].getOrElse(0)
    val selectedIndices = (json \ "selectedIndices").asOpt[List[Int]].getOrElse(List.empty)

    val board = Board(cards)
    Game(board, players, currentPlayerIndex, selectedIndices)
  }

  def toJson(game: ModelInterface): JsValue = Json.obj(
    "players" -> game.players.map { p =>
      Json.obj(
        "name" -> p.name,
        "score" -> p.score
      )
    },
    "cards" -> game.board.cards.map { c =>
      Json.obj(
        "value" -> c.value,
        "isRevealed" -> c.isRevealed
      )
    },
    "currentPlayerIndex" -> game.currentPlayerIndex,
    "selectedIndices" -> game.selectedIndices
  )
}
