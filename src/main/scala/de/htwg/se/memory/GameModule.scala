
package de.htwg.se.memory

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import net.codingwell.scalaguice.ScalaModule
import de.htwg.se.memory.model.{ModelInterface, Game, Player, Board}
import de.htwg.se.memory.model.fileIO.FileIOInterface
import de.htwg.se.memory.model.fileIO.json.FileIOJson
import de.htwg.se.memory.model.fileIO.xml.FileIOXML
import de.htwg.se.memory.factory.DynamicCardSetFactory
import de.htwg.se.memory.controller.ControllerInterface
import de.htwg.se.memory.controller.Controller
import de.htwg.se.memory.view._


class GameModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    val factory = new DynamicCardSetFactory(6)
    val cards = factory.createCards()
    val board = Board(cards)
    val players = List(Player("Spieler 1"), Player("Spieler 2"))
    val game = Game(board, players)

    bind[ModelInterface].toInstance(game)
    bind[ControllerInterface].to[Controller].in(Scopes.SINGLETON)
    bind(classOf[Gui]).toProvider(classOf[GuiProvider])
    bind(classOf[Tui])

    bind[FileIOInterface].to[FileIOJson] //to[FileIOXML]
  }
}

