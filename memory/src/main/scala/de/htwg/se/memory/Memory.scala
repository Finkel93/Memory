package de.htwg.se.memory

import de.htwg.se.memory.model.{Board, Card, Game, Player}
import de.htwg.se.memory.controller.Controller
import de.htwg.se.memory.view.Tui

import scala.util.Random  // für shuffle

object Memory {
  def main(args: Array[String]): Unit = {
    val values = List("A", "A", "B", "B", "C", "C", "D", "D")
    val cards = Random.shuffle(values).map(Card(_))
    val board = Board(cards)
    val players = List(Player("Spieler 1"), Player("Spieler 2"))
    val game = Game(board, players)

    val controller = new Controller(game)
    val tui = new Tui(controller)

    tui.run()
  }
}