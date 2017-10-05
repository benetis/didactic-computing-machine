object CrosswordCross extends App{
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

  def freqList(words: Vector[String]): Map[Int, Vector[String]] = {
    words.groupBy(_.length)
  }
}
