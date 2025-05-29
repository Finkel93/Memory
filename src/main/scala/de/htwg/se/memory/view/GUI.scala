package de.htwg.se.memory.view

import scala.swing._
import scala.swing.event._
import de.htwg.se.memory.util.Observer
import de.htwg.se.memory.controller.Controller

class Gui(val controller: Controller, exitCallback: () => Unit) extends MainFrame with Observer {
  controller.add(this)
  private var waiting = false // Flag ob Timer läuft, damit update nicht mehrfach Timer startet
  title = "Memory Game"
  preferredSize = new Dimension(600, 400)

  // Fenster-Schließen-Event
  override def closeOperation(): Unit = {
    exitCallback()
    super.closeOperation()
  }

  // Status-Label für aktuellen Spieler
  val statusLabel = new Label(s"Spieler am Zug: ${controller.currentPlayer.name}")

  // Panel für die Karten
  val cardPanel = new GridPanel(4, 3) {
    border = Swing.EmptyBorder(10)
  }

  // Karten-Buttons erstellen und hinzufügen
  updateCards()

  // Layout mit StatusLabel oben und Karten darunter
  contents = new BorderPanel {
    layout(statusLabel) = BorderPanel.Position.North
    layout(cardPanel) = BorderPanel.Position.Center
  }


  val undoButton = new Button("Undo") {
    reactions += {
      case ButtonClicked(_) => controller.undo()
    }
  }

  val redoButton = new Button("Redo") {
    reactions += {
      case ButtonClicked(_) => controller.redo()
    }
  }

  val buttonPanel = new FlowPanel {
    contents += undoButton
    contents += redoButton
  }

  contents = new BorderPanel {
    layout(statusLabel) = BorderPanel.Position.North
    layout(cardPanel) = BorderPanel.Position.Center
    layout(buttonPanel) = BorderPanel.Position.South
  }

  private def updateCards(): Unit = {
    cardPanel.contents.clear()
    controller.gameState.board.cards.zipWithIndex.foreach { case (card, idx) =>
      val button = new Button {
        text = if (card.isRevealed) card.value.toString else "?"
        enabled = !card.isRevealed
      }
      button.reactions += {
        case ButtonClicked(_) =>
          controller.handleInput(idx)
      }
      cardPanel.contents += button
    }

    cardPanel.revalidate() // Layout neu berechnen

    cardPanel.repaint()    // Panel neu zeichnen
  }




  import javax.swing.Timer
  import java.awt.event.ActionListener

  override def update: Unit = {
    Swing.onEDT {
      statusLabel.text = s"Spieler am Zug: ${controller.currentPlayer.name}"
      updateCards()
      repaint()

      if (controller.gameState.selectedIndices.size == 2 && !waiting) {
        waiting = true
        val timer = new Timer(1000, null)
        timer.addActionListener { _ =>
          controller.nextTurn()
          waiting = false
          timer.stop()
        }
        timer.setRepeats(false)
        timer.start()
      }
    }
  }





  def spinWait(ms: Long): Unit = {
    val start = System.currentTimeMillis()
    while (System.currentTimeMillis() - start < ms) {
      // Spin wait - tut nichts außer Zeit abwarten
    }
  }

  pack()
  centerOnScreen()
}