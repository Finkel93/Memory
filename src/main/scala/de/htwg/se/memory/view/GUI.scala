package de.htwg.se.memory.view

import scala.swing._
import scala.swing.event._
import de.htwg.se.memory.util.Observer
import de.htwg.se.memory.controller.Controller

class Gui(val controller: Controller, exitCallback: () => Unit) extends MainFrame with Observer {
  controller.add(this)

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

  private def updateCards(): Unit = {
    cardPanel.contents.clear()

    controller.gameState.board.cards.zipWithIndex.foreach { case (card, idx) =>
      val button = new Button {
        text = if (card.isRevealed) card.value.toString else "?"
        enabled = !card.isRevealed
      }
      button.reactions += {
        case ButtonClicked(_) => controller.handleInput(idx)
      }
      cardPanel.contents += button
    }

    cardPanel.revalidate() // Layout neu berechnen
    cardPanel.repaint()    // Panel neu zeichnen
  }


  override def update: Unit = {
    Swing.onEDT {
      statusLabel.text = s"Spieler am Zug: ${controller.currentPlayer.name}"
      updateCards()
      repaint()

      // Prüfe ob zwei Karten aufgedeckt sind
      if (controller.gameState.selectedIndices.size == 2) {
        // Verzögerung von 1 Sekunde, bevor nextTurn aufgerufen wird
        val timer = new javax.swing.Timer(1000, new java.awt.event.ActionListener {
          override def actionPerformed(e: java.awt.event.ActionEvent): Unit = {
            controller.nextTurn()
          }
        })
        timer.setRepeats(false)
        timer.start()
      }
    }
  }


  pack()
  centerOnScreen()
}