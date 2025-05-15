import de.htwg.se.memory.factory._
import de.htwg.se.memory.model._
import de.htwg.se.memory.controller.Controller
import de.htwg.se.memory.view.Tui

object Memory {
  def main(args: Array[String]): Unit = {
    val factory = new DynamicCardSetFactory(6)
    val cards = factory.createCards()
    val board = Board(cards)
    val players = List(Player("Spieler 1"), Player("Spieler 2"))
    val game = Game(board, players)

    val controller = new Controller(game)
    val tui = new Tui(controller)
    tui.run()
  }
}
