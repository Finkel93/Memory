package de.htwg.se.memory

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.google.inject.{Guice, Injector}
import de.htwg.se.memory.model.{ModelInterface, Game, Player, Board}
import de.htwg.se.memory.model.fileIO.FileIOInterface
import de.htwg.se.memory.model.fileIO.xml.FileIOXML
import de.htwg.se.memory.controller.ControllerInterface
import de.htwg.se.memory.view.{Gui, Tui}

class GameModuleTest extends AnyFlatSpec with Matchers {

  val injector: Injector = Guice.createInjector(new GameModule)

  "GameModule" should "bind ModelInterface to Game instance" in {
    val model = injector.getInstance(classOf[ModelInterface])
    model shouldBe a[Game]

    // Test that the game is properly initialized
    val game = model.asInstanceOf[Game]
    game.board should not be null
    game.players should have size 2
    game.players.head.name should be("Spieler 1")
    game.players(1).name should be("Spieler 2")
  }

  it should "bind ControllerInterface as singleton" in {
    val controller1 = injector.getInstance(classOf[ControllerInterface])
    val controller2 = injector.getInstance(classOf[ControllerInterface])

    controller1 should be theSameInstanceAs controller2
  }

  /*it should "bind FileIOInterface to FileIOXML" in {
    val fileIO = injector.getInstance(classOf[FileIOInterface])
    fileIO shouldBe a[FileIOXML]
  }

   */

  /*it should "provide Gui instance" in {
    val gui = injector.getInstance(classOf[Gui])
    gui should not be null
  }*/

  it should "provide Tui instance" in {
    val tui = injector.getInstance(classOf[Tui])
    tui should not be null
  }

  it should "create board with cards from DynamicCardSetFactory" in {
    val model = injector.getInstance(classOf[ModelInterface])
    val game = model.asInstanceOf[Game]

    game.board.cards should not be empty
    game.board.cards should have size 12
  }

  it should "initialize players correctly" in {
    val model = injector.getInstance(classOf[ModelInterface])
    val game = model.asInstanceOf[Game]

    game.players should contain allOf (
      Player("Spieler 1"),
      Player("Spieler 2")
    )
  }
}


class GameModuleMockTest extends AnyFlatSpec with Matchers {

  /*"GameModule" should "handle dependency injection gracefully" in {
    noException should be thrownBy {
      val injector = Guice.createInjector(new GameModule)
      injector.getInstance(classOf[ModelInterface])
      injector.getInstance(classOf[ControllerInterface])
      injector.getInstance(classOf[FileIOInterface])
      injector.getInstance(classOf[Gui])
      injector.getInstance(classOf[Tui])
    }
  }*/
}