package me.benetis

object Main extends App {

  case class Square(x: Int, y: Int)

  type PlacedQueens = Vector[Square]

  def isChecked(p1: Square, p2: Square): Boolean = {
    p1.x == p2.x ||
    p1.y == p2.y ||
    Math.abs(p1.x - p2.x) == Math.abs(p1.y - p2.y)
  }

  val wholeBoard: Vector[Square] = {
    Range
      .inclusive(1, 8)
      .flatMap(x => {
        Range
          .inclusive(1, 8)
          .map(y => {
            Square(x, y)
          })
      })
      .toVector
  }

  def queensCheckedSquares(queen: Square): Vector[Square] = {
    wholeBoard.filter(sq => isChecked(queen, sq))
  }

  def canBePlaced(queenToPlace: Square,
                  availableSquares: Vector[Square]): Option[Vector[Square]] = {

    if (availableSquares.nonEmpty) {
      if (availableSquares.contains(queenToPlace))
        Some(
          availableSquares.toSet
            .diff(queensCheckedSquares(queenToPlace).toSet)
            .toVector
        )
      else
        None
    } else {
      None
    }
  }

  def placeQueens(toPlace: Int,
                  available: Vector[Square]): Option[PlacedQueens] = {

    if (toPlace <= 0) {
      Some(Vector.empty[Square])
    } else {
      val result = available.foldLeft(Vector.empty[Square])(
        (prev, queenToPlace: Square) => {
          val afterQueen = canBePlaced(queenToPlace, available)

          afterQueen match {
            case Some(nextSquares) =>
              placeQueens(toPlace - 1, nextSquares) match {
                case Some(value) =>
                  queenToPlace +: value
                case None => prev
              }
            case None => prev
          }

        }
      )

      if (result.isEmpty)
        None
      else
        Some(result)
    }
  }

  def printQueens(placedQueens: PlacedQueens): Unit = {

    println(placedQueens)

    Range
      .inclusive(1, 8)
      .foreach(x => {
        Range
          .inclusive(1, 8)
          .foreach(y => {
            if (placedQueens.contains(Square(x, y)))
              print(s"X ")
            else
              print(s"- ")
          })
        println()
      })
  }

  placeQueens(8, wholeBoard) match {
    case Some(value) => printQueens(value)
    case None        => println("no solutions are possible")
  }
}
