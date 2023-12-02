object CrosswordCross extends App {

  sealed trait Cell

  case object V extends Cell //vowelCell
  case object C extends Cell //consolantCell
  case object N extends Cell
  case object L extends Cell

  val wordsList = Vector(
    "pakili",
    "kilimas",
    "mama",
    "alus",
    "musė",
    "saulė",
    "ainis",
    "dugnas"
  )

  val vowels = Vector("a", "e", "i", "o", "u", "a", "ą", "e", "ę", "ė", "y", "į", "o", "ū", "ų")

  //9x9
  val inputGrid = Vector(
    Vector(N, N, N, N, N, C, N, N, N),
    Vector(N, N, N, N, N, V, N, N, N),
    Vector(C, C, V, C, V, C, V, N, N),
    Vector(N, C, N, N, N, V, N, N, N),
    Vector(N, V, C, V, N, C, V, C, V),
    Vector(N, C, N, V, N, V, N, V, N),
    Vector(C, V, C, C, V, C, N, V, N),
    Vector(N, C, N, V, N, N, N, C, N),
    Vector(N, N, N, C, N, N, N, V, N)
  )

  def toVowelsAndConsolants(words: Vector[String]): Vector[(Int, Int, String)] = {

    def countVowels(word: String): Int = {
      word.foldLeft(0)((res: Int, c: Char) => {
        val contains: Int = if (vowels.contains(c.toString)) 1 else 0
        res + contains
      })
    }

    words.map(myWord => (myWord.length - countVowels(myWord), countVowels(myWord), myWord))
  }

  inputGrid.foreach((gridLine: Vector[Cell]) => {

  })




  println(toVowelsAndConsolants(wordsList))


}
