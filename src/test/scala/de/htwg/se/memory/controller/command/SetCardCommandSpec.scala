package de.htwg.se.memory.controller.command

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.memory.controller.Controller
import de.htwg.se.memory.model.{Card, Board, Game, Player}
import de.htwg.se.memory.controller.strategy.KeepOpenStrategy

class SetCardCommandSpec extends AnyWordSpec with Matchers {

  "A SetCardCommand" should {
    "do, undo and redo correctly" in {
      // Arrange: Erstelle ein Spiel mit 2 Karten (ein Paar)
      val player1 = Player("Alice")
      val player2 = Player("Bob")
      val cards = List(
        Card("A", isRevealed = false),
        Card("A", isRevealed = false)
      )
      val board = Board(cards)
      val game = Game(board, List(player1, player2), currentPlayerIndex = 0, selectedIndices = List())

      val controller = new Controller(game)
      //controller.setMatchStrategy(new KeepOpenStrategy)

      val cmd = new SetCardCommand(0, controller)

      // Act & Assert

      // 1. doStep aufrufen
      cmd.doStep()
      controller.gameState.board.cards(0).isRevealed shouldBe true

      // 2. Eine zweite Karte aufdecken, um nextTurn() zu triggern
      val cmd2 = new SetCardCommand(1, controller)
      cmd2.doStep()
      controller.gameState.board.cards(1).isRevealed shouldBe true

      // 3. undoStep für cmd2
      cmd2.undoStep()
      controller.gameState.board.cards(1).isRevealed shouldBe false

      // 4. redoStep für cmd2
      cmd2.redoStep()
      controller.gameState.board.cards(1).isRevealed shouldBe true
    }
  }
}
