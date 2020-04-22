package me.benetis.initial_states

import me.benetis.{LifeState, Point}

object RandomState {

  def randomState(): LifeState = {

    val rows = Range.inclusive(10, 20)
    val columns = Range.inclusive(10, 20)

    val evenR = rows.filter(_ % 2 == 0)
    val evenC = rows.filter(_ % 2 == 0)

    rows.flatMap(i => columns.map(j => Point(i, j))).toSet

  }

}
