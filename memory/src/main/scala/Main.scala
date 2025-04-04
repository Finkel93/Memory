@main
def main(): Unit = {

  val height = 26    // 4x + 2
  val width = 72     // 7x + 2

  spielerErstellen(1, 4)

  feldErstellen(height, width)

  spielerErstellen(2, 4)

}


def spielerErstellen (id:Int, karten:Int) : Unit = {

  val top = "Spieler " + id + ":\n ----- \n"
  val mid = "|     |\n" * 2
  val bottom = " ----- \nPaare: "+ karten  +" \n"
  printf("%s%s%s", top, mid, bottom)
}

def feldErstellen (height:Int, width:Int) : Unit = {
  val rand = " " + "-" * (width - 2) + " "
  println(rand)
  for (i <- 0 to height + 1) yield {
    if (i % 4 == 0 || i % 4 == 3) {
      val ausgabe = "|" + (" " + "-----" + " ") * ((width - 2) / 7) + "|"
      println(ausgabe)
    } else {
      val ausgabe = "|" + ("|" + "     " + "|") * ((width - 2) / 7) + "|"
      println(ausgabe)
    }
  }
  println(rand)
}