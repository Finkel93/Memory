package de.htwg.se.memory.model

trait ModelInterface {

  def board: BoardInterface
  def players: List[PlayerInterface]
  def currentPlayerIndex: Int
  def selectedIndices: List[Int]

  def selectCard(index: Int): GameInterface
  def isGameOver: Boolean
  def getWinners: List[PlayerInterface]
  def printResult(winners: List[PlayerInterface]): String

  def update(
              board: BoardInterface = this.board,
              players: List[PlayerInterface] = this.players,
              currentPlayerIndex: Int = this.currentPlayerIndex,
              selectedIndices: List[Int] = this.selectedIndices
            ): GameInterface
}
