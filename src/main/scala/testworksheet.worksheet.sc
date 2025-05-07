
case class Karte(Symbol: String, isFaceUp: Boolean = false)


case class Feld(Karte: Seq[Karte]) {
  def karteUmdrehen(index: Int): Feld = {
    val updatedCards = Karte.updated(index, Karte(index).copy(isFaceUp = !Karte(index).isFaceUp))
    Feld(updatedCards)
  }
}

case class Spieler(id: Int, Stapel: Int = 0, istAmZug: Boolean) {
  def paarZufügen(): Spieler = copy(Stapel = Stapel + 1)
  def nächsterZug(): Spieler = copy(istAmZug = !istAmZug)
}


val player1 = Spieler(1, istAmZug = true)
val player2 = Spieler(2, istAmZug = false)

val initialBoard = Feld(Seq(
  Karte("A"), Karte("A"),
  Karte("B"), Karte("B"),
  Karte("C"), Karte("C")
))

player1.nächsterZug()
player1.paarZufügen()

initialBoard.karteUmdrehen(2)
println(initialBoard)
