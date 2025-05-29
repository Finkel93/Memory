import de.htwg.se.memory.factory._
import de.htwg.se.memory.model._
import de.htwg.se.memory.controller.Controller
import de.htwg.se.memory.view.{Tui, Gui}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Memory {
  @volatile private var running = true

  def main(args: Array[String]): Unit = {
    // Spiel vorbereiten
    val factory = new DynamicCardSetFactory(6)
    val cards = factory.createCards()
    val board = Board(cards)
    val players = List(Player("Spieler 1"), Player("Spieler 2"))
    val game = Game(board, players)
    val controller = new Controller(game)

    // GUI anzeigen
    val gui = new Gui(controller, () => running = false)
    gui.visible = true

    // TUI starten
    val tui = new Tui(controller)
    tui.start()

    // TUI-Eingabelogik in einem eigenen Thread ausführen
    Future {
      while (running && !controller.isGameOver) {
        val input = scala.io.StdIn.readLine()
        if (input == "q") {
          running = false
        } else {
          tui.handleInput(input)
        }
      }
    }

    // Hauptthread blockieren, bis das Spiel vorbei ist
    while (running) {
      Thread.sleep(100)
    }

    println("Spiel wurde beendet.")
    System.exit(0)
  }
}
