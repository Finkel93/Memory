// Datei: Memory.scala
package de.htwg.se.memory

import com.google.inject.Guice
import net.codingwell.scalaguice.InjectorExtensions._

import de.htwg.se.memory.controller.ControllerInterface
import de.htwg.se.memory.view._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Memory {
  @volatile private var running = true

  def main(args: Array[String]): Unit = {
    val injector = Guice.createInjector(new ControllerModule())
    val controller = injector.instance[ControllerInterface]

    val guiProvider = injector.instance[GuiProvider]
    guiProvider.setExitCallback(() => running = false)

    val gui = injector.instance[Gui]
    gui.visible = true

    val tui = injector.instance[Tui]
    tui.start()

    Future {
      while (running && !controller.isGameOver) {
        val input = scala.io.StdIn.readLine()
        if (input == "q") running = false else tui.handleInput(input)
      }
    }

    while (running) {
      Thread.sleep(100)
    }

    println("Spiel wurde beendet.")
    System.exit(0)
  }
}