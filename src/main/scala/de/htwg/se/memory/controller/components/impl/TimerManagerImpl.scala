package de.htwg.se.memory.controller.components.impl

import de.htwg.se.memory.controller.components.TimerManager
import javax.swing.Timer
import java.awt.event.ActionListener

// Isolierte Timer-Komponente (keine External Dependencies)
class TimerManagerImpl extends TimerManager {
  private var currentTimer: Option[Timer] = None

  override def startTimer(durationMs: Int, onComplete: () => Unit): Unit = {
    stopTimer() // Cleanup existing timer

    currentTimer = Some(new Timer(durationMs, new ActionListener {
      override def actionPerformed(e: java.awt.event.ActionEvent): Unit = {
        onComplete()
        stopTimer()
      }
    }))

    currentTimer.foreach { timer =>
      timer.setRepeats(false)
      timer.start()
    }
  }

  override def stopTimer(): Unit = {
    currentTimer.foreach(_.stop())
    currentTimer = None
  }

  override def isRunning: Boolean = currentTimer.exists(_.isRunning)
}