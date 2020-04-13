package me.benetis

import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.layout.{HBox, Pane}
import scalafx.scene.{Group, Scene}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Rectangle

object View {

  val deadCell: Rectangle = Rectangle(15, 15, Color.rgb(100, 100, 100))

  val cellGroup: Group = new Group()

  def renderCells(lifeState: LifeState): Unit = {
    cellGroup.children = lifeState.map(cellAt)
  }

  def cellAt(point: Point): Rectangle = {
    val scale = 15
    val c = Rectangle(scale, scale, PaleGreen)

    c.x = point.x * scale
    c.y = point.y * scale

    c
  }
}
