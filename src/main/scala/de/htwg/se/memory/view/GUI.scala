package de.htwg.se.memory.view

import scala.swing._
import scala.swing.event._
import de.htwg.se.memory.util.Observer
import de.htwg.se.memory.controller.ControllerInterface
import com.google.inject.Inject

class Gui @Inject() (val controller: ControllerInterface, exitCallback: () => Unit)
  extends MainFrame with Observer {
  controller.add(this)
  title = "Memory Game"
  preferredSize = new Dimension(600, 400)

  val leftScoreLabel = new Label("Spieler 1 : 0")
  val rightScoreLabel = new Label("Spieler 2 : 0")
  val statusLabel = new Label(s"Spieler am Zug: ${controller.currentPlayer.name}")

  val cardPanel = new GridPanel(4, 3) {
    border = Swing.EmptyBorder(10)
  }

  val undoButton = new Button("Undo") {
    enabled = false
    reactions += {
      case ButtonClicked(_) => controller.undo()
    }
  }

  val redoButton = new Button("Redo") {
    enabled = false
    reactions += {
      case ButtonClicked(_) => controller.redo()
    }
  }

  val buttonPanel = new FlowPanel {
    contents += undoButton
    contents += redoButton
  }

  contents = new BorderPanel {
    layout(new BorderPanel {
      layout(leftScoreLabel) = BorderPanel.Position.West
      layout(statusLabel)    = BorderPanel.Position.Center
      layout(rightScoreLabel)= BorderPanel.Position.East
    }) = BorderPanel.Position.North
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
    cardPanel.revalidate()
    cardPanel.repaint()
  }

  override def update: Unit = {
    Swing.onEDT {
      val players = controller.gameState.players
      val scores = players.map(p => s"${p.name}: ${p.score}")

      leftScoreLabel.text = scores.headOption.getOrElse("")
      rightScoreLabel.text = if (scores.size > 1) scores(1) else ""

      if (controller.isGameOver) {
        val winners = controller.getWinners
        statusLabel.text =
          if (winners.size == 1)
            s"Spiel vorbei! Gewinner: ${winners.head.name}"
          else
            s"Spiel vorbei! Unentschieden zwischen: ${winners.map(_.name).mkString(", ")}"
      } else {
        statusLabel.text = s"Spieler am Zug: ${controller.currentPlayer.name}"
      }

      undoButton.enabled = controller.canUndo
      redoButton.enabled = controller.canRedo

      updateCards()
    }
  }

  updateCards()
  pack()
  centerOnScreen()
}
