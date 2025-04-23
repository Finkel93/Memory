import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class InputHelperSpec extends AnyWordSpec with Matchers {

  "InputHelper" should {

    "retry if input is invalid and eventually accept valid input" in {
      val cards = List(Card("A"))
      val game = Game(Board(cards), List(Player("P1")))

      // Simuliere ungültige Eingabe ("invalid"), gefolgt von einer gültigen Eingabe ("0")
      val input = Iterator("invalid", "0")
      val index = InputHelper.getInput("Wähle Karte: ", game, () => input.next())

      // Überprüfe, dass der Index 0 ist
      index shouldBe 0
    }

    "retry on non-integer input" in {
      val cards = List(Card("A"), Card("B"))
      val game = Game(Board(cards), List(Player("P1")))

      // Simuliere eine ungültige Eingabe ("abc"), gefolgt von einer gültigen Eingabe ("1")
      val input = Iterator("abc", "1")
      val index = InputHelper.getInput("Wähle Karte: ", game, () => input.next())

      // Überprüfe, dass der Index 1 ist
      index shouldBe 1
    }

    "retry if index is out of bounds (too high)" in {
      val cards = List(Card("A")) // Nur Index 0 erlaubt
      val game = Game(Board(cards), List(Player("P1")))

      // Simuliere eine Eingabe, die aus dem gültigen Bereich (5) hinausgeht, gefolgt von einem gültigen Index (0)
      val input = Iterator("5", "0")
      val index = InputHelper.getInput("Wähle Karte: ", game, () => input.next())

      // Überprüfe, dass der Index 0 ist
      index shouldBe 0
    }

    "retry if index is negative" in {
      val cards = List(Card("A"), Card("B"))
      val game = Game(Board(cards), List(Player("P1")))

      // Simuliere eine ungültige Eingabe (-1), gefolgt von einer gültigen Eingabe (1)
      val input = Iterator("-1", "1")
      val index = InputHelper.getInput("Wähle Karte: ", game, () => input.next())

      // Überprüfe, dass der Index 1 ist
      index shouldBe 1
    }

    "retry if card already revealed" in {
      val cards = List(Card("A", isRevealed = true), Card("B"))
      val game = Game(Board(cards), List(Player("P1")))

      // Simuliere ungültige Eingabe (0 ist bereits aufgedeckt), gefolgt von einer gültigen Eingabe (1)
      val input = Iterator("0", "1")
      val index = InputHelper.getInput("Wähle Karte: ", game, () => input.next())

      // Überprüfe, dass der Index 1 ist
      index shouldBe 1
    }

    "retry if card already selected" in {
      val cards = List(Card("A"), Card("B"))
      val game = Game(Board(cards), List(Player("P1")), selectedIndices = List(0))

      // Simuliere ungültige Eingabe (0 wurde bereits gewählt), gefolgt von einer gültigen Eingabe (1)
      val input = Iterator("0", "1")
      val index = InputHelper.getInput("Wähle Karte: ", game, () => input.next())

      // Überprüfe, dass der Index 1 ist
      index shouldBe 1
    }

    "accept valid input on first try" in {
      val cards = List(Card("A"), Card("B"))
      val game = Game(Board(cards), List(Player("P1")))

      // Simuliere direkt eine gültige Eingabe (1)
      val input = Iterator("1")
      val index = InputHelper.getInput("Wähle Karte: ", game, () => input.next())

      // Überprüfe, dass der Index 1 ist
      index shouldBe 1
    }
  }
}
