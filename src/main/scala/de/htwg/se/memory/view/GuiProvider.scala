package de.htwg.se.memory.view

import com.google.inject.{Inject, Provider}
import de.htwg.se.memory.controller.ControllerInterface

class GuiProvider @Inject()(controller: ControllerInterface) extends Provider[Gui] {
  override def get(): Gui = new Gui(controller, exitCallback)

  private var exitCallback: () => Unit = () => {}

  def setExitCallback(callback: () => Unit): Unit = {
    exitCallback = callback
  }
}
