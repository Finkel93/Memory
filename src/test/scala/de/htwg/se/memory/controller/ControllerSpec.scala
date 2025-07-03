import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito._
import org.mockito.Mockito
import org.mockito.ArgumentMatchers._
import de.htwg.se.memory.model._
import de.htwg.se.memory.controller._
import de.htwg.se.memory.controller.command._
import de.htwg.se.memory.model.fileIO._
import de.htwg.se.memory.controller.strategy._

import javax.swing.Timer

class ControllerSpec extends AnyWordSpec with Matchers {

  "Controller" should {


    
    "execute command and update stacks correctly" in {
      val gameState = mock(classOf[ModelInterface])
      val fileIO = mock(classOf[FileIOInterface])
      val controller = new Controller(gameState, fileIO)

      val command = mock(classOf[Command])
      controller.executeCommand(command)

      verify(command).doStep()
      controller.canUndo shouldBe true
      controller.canRedo shouldBe false
    }

    "stop an active timer and call nextTurn if handleInput is called while timer is running" in {
      // Zwei ungleiche Karten
      val cards = List(
        Card("X"), Card("Y"),
        Card("Z"), Card("A"),
        Card("B"), Card("C"),
        Card("D"), Card("E"),
        Card("F"), Card("G"),
        Card("H"), Card("I")
      )

      val board = Board(cards)
      val players = List(Player("Player 1"), Player("Player 2"))
      val game = Game(board, players)

      val mockFileIO = Mockito.mock(classOf[FileIOInterface])
      val controller = new Controller(game, mockFileIO)

      // Zuerst zwei unterschiedliche Karten aufdecken → Timer startet
      controller.handleInput(0)
      controller.handleInput(1)

      // Timer sollte nun laufen
      val maybeTimer = controller.hideCardsTimer
      maybeTimer should not be empty
      val timer = maybeTimer.get
      timer.isRunning shouldBe true

      // Spy auf die Methode nextTurn (alternativ durch Zustand prüfen)
      val oldPlayerIndex = controller.gameState.currentPlayerIndex

      // Rufe handleInput auf, während Timer noch läuft
      controller.handleInput(2)

      // Timer muss gestoppt worden sein
      timer.isRunning shouldBe false

      // Der Spielerwechsel (nextTurn) sollte erfolgt sein
      controller.gameState.currentPlayerIndex should not be oldPlayerIndex
    }

    "start a timer when two selected cards do not match" in {
      // Karten mit unterschiedlichem Wert
      val cards = List(
        Card("A"), Card("B"),
        Card("C"), Card("D"),
        Card("E"), Card("F"),
        Card("G"), Card("H"),
        Card("I"), Card("J"),
        Card("K"), Card("L")
      )

      // Stelle sicher, dass Karten "A" und "B" an Position 0 und 1 stehen
      val board = Board(cards)
      val players = List(Player("P1"), Player("P2"))
      val game = Game(board, players)

      // Mock FileIO (wird nicht verwendet in diesem Test)
      val mockFileIO = Mockito.mock(classOf[FileIOInterface])

      val controller = new Controller(game, mockFileIO)

      // handleInput simuliert das Aufdecken von zwei nicht passenden Karten
      controller.handleInput(0) // Karte A
      controller.handleInput(1) // Karte B (nicht gleich A)

      // Timer sollte jetzt gestartet sein
      controller.hideCardsTimer should not be empty
      controller.hideCardsTimer.get.isRunning shouldBe true
    }
    "startHideCardsTimer should schedule nextTurn and stop timer after timeout" in {
      // Zwei ungleiche Karten
      val cards = List(
        Card("1"), Card("2"),
        Card("3"), Card("4"),
        Card("5"), Card("6"),
        Card("7"), Card("8"),
        Card("9"), Card("10"),
        Card("11"), Card("12")
      )

      val board = Board(cards)
      val players = List(Player("Player 1"), Player("Player 2"))
      val game = Game(board, players)

      val mockFileIO = Mockito.mock(classOf[FileIOInterface])
      val controller = new Controller(game, mockFileIO)

      // Decke zwei verschiedene Karten auf
      controller.handleInput(0)
      controller.handleInput(1)

      // Timer sollte jetzt laufen
      val maybeTimer = controller.hideCardsTimer
      maybeTimer should not be empty
      val timer = maybeTimer.get
      timer.isRunning shouldBe true

      // Speichere aktuellen PlayerIndex (wird sich nach Timer ändern)
      val oldPlayerIndex = controller.gameState.currentPlayerIndex

      // Simuliere Ablauf des Timers manuell (wie in Zeile 113-117)
      val action = timer.getActionListeners.head
      action.actionPerformed(new java.awt.event.ActionEvent(timer, 0, "timer"))

      // Sicherstellen, dass:
      // 1. Timer gestoppt wurde (Zeile 114)
      timer.isRunning shouldBe false

      // 2. hideCardsTimer auf None gesetzt wurde (Zeile 116)
      controller.hideCardsTimer shouldBe None

      // 3. nextTurn wurde aufgerufen → Spielerwechsel (Zeile 115)
      controller.gameState.currentPlayerIndex should not be oldPlayerIndex
    }

    "startHideCardsTimer should stop running timer before starting a new one" in {
      val cards = List(
        Card("1"), Card("1"), // gleiche Karten, damit Timer nicht gleich weg ist
        Card("3"), Card("4"),
        Card("5"), Card("6"),
        Card("7"), Card("8"),
        Card("9"), Card("10"),
        Card("11"), Card("12")
      )
      val board = Board(cards)
      val players = List(Player("Player 1"), Player("Player 2"))
      val game = Game(board, players)

      val mockFileIO = Mockito.mock(classOf[FileIOInterface])
      val controller = new Controller(game, mockFileIO)

      // Erst mal Timer starten mit zwei unterschiedlichen Karten (0 und 3)
      controller.handleInput(0)
      controller.handleInput(3)
      val firstTimer = controller.hideCardsTimer.get
      firstTimer.isRunning shouldBe true

      // Jetzt startHideCardsTimer erneut aufrufen — es sollte den alten Timer stoppen
      controller.startHideCardsTimer()

      val secondTimer = controller.hideCardsTimer.get
      secondTimer.isRunning shouldBe true

      // Prüfe, dass der erste Timer nicht mehr läuft (wurde gestoppt)
      firstTimer.isRunning shouldBe false

      // Der zweite Timer ist ein neuer Timer und läuft jetzt
      secondTimer should not be firstTimer
    }


    "selectCard throws exception if card is revealed" in {
      val card = mock(classOf[CardInterface])
      when(card.isRevealed).thenReturn(true)
      val board = mock(classOf[BoardInterface])
      when(board.cards).thenReturn(List(card))

      val gameState = mock(classOf[ModelInterface])
      when(gameState.board).thenReturn(board)

      val fileIO = mock(classOf[FileIOInterface])
      val controller = new Controller(gameState, fileIO)

      an [IllegalArgumentException] should be thrownBy controller.selectCard(0)
    }
    "execute a command and push it to the undo stack" in {
      val gameState = mock(classOf[ModelInterface])
      val fileIO = mock(classOf[FileIOInterface])
      val controller = new Controller(gameState, fileIO)

      val command = mock(classOf[Command])
      controller.executeCommand(command)

      verify(command, times(1)).doStep()
      controller.canUndo shouldBe true
      controller.canRedo shouldBe false
    }

    "undo a command and move it to the redo stack" in {
      val gameState = mock(classOf[ModelInterface])
      val fileIO = mock(classOf[FileIOInterface])
      val controller = new Controller(gameState, fileIO)

      val command = mock(classOf[Command])
      controller.executeCommand(command)
      controller.undo()

      verify(command, times(1)).undoStep()
      controller.canUndo shouldBe false
      controller.canRedo shouldBe true
    }

    "redo a command and move it back to the undo stack" in {
      val gameState = mock(classOf[ModelInterface])
      val fileIO = mock(classOf[FileIOInterface])
      val controller = new Controller(gameState, fileIO)

      val command = mock(classOf[Command])
      controller.executeCommand(command)
      controller.undo()
      controller.redo()

      verify(command, times(1)).redoStep()
      controller.canUndo shouldBe true
      controller.canRedo shouldBe false
    }

    "not undo if the undo stack is empty" in {
      val gameState = mock(classOf[ModelInterface])
      val fileIO = mock(classOf[FileIOInterface])
      val controller = new Controller(gameState, fileIO)

      noException should be thrownBy controller.undo()
      controller.canUndo shouldBe false
    }

    "not redo if the redo stack is empty" in {
      val gameState = mock(classOf[ModelInterface])
      val fileIO = mock(classOf[FileIOInterface])
      val controller = new Controller(gameState, fileIO)

      noException should be thrownBy controller.redo()
      controller.canRedo shouldBe false
    }

    "clear redo stack when a new command is executed" in {
      val gameState = mock(classOf[ModelInterface])
      val fileIO = mock(classOf[FileIOInterface])
      val controller = new Controller(gameState, fileIO)

      val command1 = mock(classOf[Command])
      val command2 = mock(classOf[Command])

      controller.executeCommand(command1)
      controller.undo()
      controller.canRedo shouldBe true

      controller.executeCommand(command2)
      controller.canRedo shouldBe false
    }

    "delegate boardView to gameState.board.displayCards" in {
      val mockBoard = mock(classOf[BoardInterface])
      val mockModel = mock(classOf[ModelInterface])
      val fileIO = mock(classOf[FileIOInterface])

      val cards = List.empty[Card]
      when(mockModel.board).thenReturn(mockBoard)
      when(mockBoard.cards).thenReturn(cards)
      when(mockBoard.displayCards(cards)).thenReturn("mocked board view")

      val controller = new Controller(mockModel, fileIO)

      controller.boardView shouldBe "mocked board view"
      verify(mockBoard, times(1)).displayCards(cards)
    }

    "delegate getWinners to gameState.getWinners" in {
      val mockPlayer = mock(classOf[PlayerInterface])
      when(mockPlayer.name).thenReturn("Player1")
      val mockModel = mock(classOf[ModelInterface])
      val fileIO = mock(classOf[FileIOInterface])

      val expectedWinners = List(mockPlayer)
      when(mockModel.getWinners).thenReturn(expectedWinners)

      val controller = new Controller(mockModel, fileIO)

      controller.getWinners shouldBe expectedWinners
      verify(mockModel, times(1)).getWinners
    }


    "handle input and execute a SetCardCommand" in {
      val mockFileIO = mock(classOf[FileIOInterface])


      // Vorbereiteter Spielzustand: Zwei Karten mit gleichem Wert
      val cards = List(
        Card("A", isRevealed = false),
        Card("A", isRevealed = false)
      )
      val board = Board(cards)
      val players = List(Player("P1"), Player("P2"))
      val game = Game(board, players)
      val controller = new Controller(game, mockFileIO)
      controller.matchStrategy = new KeepOpenStrategy // einfacher zu testen

      controller.handleInput(0) // Karte 0
      controller.handleInput(1) // Karte 1

      // Jetzt sind zwei Karten aufgedeckt -> Match-Logik greift
      controller.gameState.board.cards(0).isRevealed shouldBe true
      controller.gameState.board.cards(1).isRevealed shouldBe true

      // currentPlayer sollte am Zug bleiben, da Match (KeepOpenStrategy)
      controller.gameState.currentPlayerIndex shouldBe 0
    }

    "handle mismatching cards and schedule hide timer" in {
      val mockFileIO = mock(classOf[FileIOInterface])


      val cards = List(
        Card("A", isRevealed = false),
        Card("B", isRevealed = false)
      )
      val board = Board(cards)
      val players = List(Player("P1"), Player("P2"))

      val game = Game(board, players)
        .selectCard(0)
        .selectCard(1)

      val controller = new Controller(game, mockFileIO)

      controller.handleInput(0)
      controller.handleInput(1)

      controller.gameState.selectedIndices should contain allOf (0, 1)

      // Timer für spätere Kartenverdeckung sollte gestartet sein
      controller.hideCardsTimer.isDefined shouldBe true
      controller.hideCardsTimer.get.isRunning shouldBe true
    }
  }
}
