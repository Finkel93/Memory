package de.htwg.se.memory.controller.strategy
import de.htwg.se.memory.controller.Controller
trait MatchStrategy {
  def handleMatch(controller: Controller, idx1: Int, idx2: Int): Unit
}
