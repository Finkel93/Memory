//import de.htwg.se.memory.factory._
//import de.htwg.se.memory.model._
//import de.htwg.se.memory.controller.Controller
//import de.htwg.se.memory.view.Tui

//object Memory {
//  def main(args: Array[String]): Unit = {
//    val factory = new DynamicCardSetFactory(6)
//    val cards = factory.createCards()
//    val board = Board(cards)
//    val players = List(Player("Spieler 1"), Player("Spieler 2"))
//    val game = Game(board, players)
//
//    val controller = new Controller(game)
//    val tui = new Tui(controller)
//    tui.run()
//  }
//}
import de.htwg.se.memory.factory._
import de.htwg.se.memory.model._
import de.htwg.se.memory.controller.Controller
import de.htwg.se.memory.view.{Tui, Gui}

import de.htwg.se.memory.factory._
import de.htwg.se.memory.model._
import de.htwg.se.memory.controller.Controller
import de.htwg.se.memory.view.{Tui, Gui}
import scala.swing.Swing

object Memory {
  @volatile private var running = true

  def main(args: Array[String]): Unit = {
    val factory = new DynamicCardSetFactory(6)
    val cards = factory.createCards()
    val board = Board(cards)
    val players = List(Player("Spieler 1"), Player("Spieler 2"))
    val game = Game(board, players)

    val controller = new Controller(game)

    // GUI erstellen und konfigurieren
    val gui = new Gui(controller, () => running = false)

    // TUI starten
    val tui = new Tui(controller)

    // GUI anzeigen
    gui.visible = true
    tui.start()

    // Hauptthread am Leben halten
    while(running) {
      Thread.sleep(100)
    }

    // Programm beenden
    System.exit(0)
  }
}