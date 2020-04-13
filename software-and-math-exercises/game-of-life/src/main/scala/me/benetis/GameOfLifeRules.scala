package me.benetis

import zio.UIO

object GameOfLifeRules {

  def nextState(state: LifeState): UIO[LifeState] = UIO {

    val selection = state
      .map(point => {
        val neighbours = howManyAliveNeighbours(point, state)
        //possible optimization, return dead neighbours here
        neighbours match {
          case x if x < 2            => None
          case x if x >= 2 && x <= 3 => Some(point)
          case x if x > 3            => None
        }
      })
      .collect { case Some(value) => value }

    val newCells = selection ++ reproduction(state)
    newCells
  }

  private def reproduction(state: LifeState): Set[Point] = {
    state
      .flatMap(point => {
        val deadNeighbours = {
          allPossibleNeighbours(point).diff(state)
        }

        deadNeighbours
          .map(deadPoint => {
            if (howManyAliveNeighbours(deadPoint, state) == 3) {
              Some(deadPoint)
            } else
              None
          })
          .collect { case Some(value) => value }
      })
  }

  /**
    +---------+
    |---------|
    |YYY------|
    |YXY------|
    +YYY------+
  **/
  private def howManyAliveNeighbours(from: Point, state: LifeState): Int = {
    state.intersect(allPossibleNeighbours(from)).size
  }

  private def allPossibleNeighbours(point: Point): Set[Point] = {
    val x = point.x
    val y = point.y
    Set(
      //Top row
      Point(x - 1, y + 1),
      Point(x, y + 1),
      Point(x + 1, y + 1),
      //Middle
      Point(x - 1, y),
      Point(x + 1, y),
      //Bottom
      Point(x - 1, y - 1),
      Point(x, y - 1),
      Point(x + 1, y - 1)
    )
  }

}
