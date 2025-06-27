package de.htwg.se.memory.controller

import de.htwg.se.memory.model.{ModelInterface, PlayerInterface}
import de.htwg.se.memory.controller.command.Command
import de.htwg.se.memory.util.Observer
import de.htwg.se.memory.controller.state.GameState


trait ControllerInterface {

  def gameState: ModelInterface
  def gameState_=(newState: ModelInterface): Unit

  def executeCommand(command: Command): Unit
  def undo(): Unit
  def redo(): Unit

  def canUndo: Boolean
  def canRedo: Boolean

  def setState(newState: GameState): Unit
  def getStateName: String

  def handleInput(input: Int): Unit

  def selectCard(index: Int): Unit

  def nextTurn(): Unit

  def isGameOver: Boolean
  def currentPlayer: PlayerInterface
  def boardView: String
  def getWinners: List[PlayerInterface]

  def add(observer: Observer): Unit
  def notifyObservers: Unit

  def startHideCardsTimer(): Unit

  def loadGame(): Unit
  def saveGame(): Unit

}
