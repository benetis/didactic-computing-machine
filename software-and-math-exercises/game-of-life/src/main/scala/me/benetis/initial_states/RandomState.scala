package me.benetis.initial_states

import me.benetis.visual.Render.RenderConfig
import me.benetis.{LifeState, Point}

object RandomState {

  def randomCenter(renderConfig: RenderConfig): LifeState = {

    val maxX: Int = (renderConfig.browserWidth / renderConfig.scale).round.toInt
    val maxY: Int =
      (renderConfig.browserHeight / renderConfig.scale).round.toInt

    val columns = Range.inclusive(
      (maxX / 2) - renderConfig.blobSize,
      (maxX / 2) + renderConfig.blobSize
    )
    val rows = Range.inclusive(
      (maxY / 2) - renderConfig.blobSize,
      (maxY / 2) + renderConfig.blobSize
    )

    randomState(rows = rows, columns = columns)

  }

  private def randomState(rows: Range, columns: Range): LifeState = {
    rows.flatMap(i => columns.map(j => Point(i, j))).toSet
  }

}
