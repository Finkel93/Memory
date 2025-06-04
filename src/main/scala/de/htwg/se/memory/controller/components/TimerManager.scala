package de.htwg.se.memory.controller.components

// Kohäsive Timer-Management Komponente (keine Dependencies zu anderen Components)
trait TimerManager {
  def startTimer(durationMs: Int, onComplete: () => Unit): Unit
  def stopTimer(): Unit
  def isRunning: Boolean
}