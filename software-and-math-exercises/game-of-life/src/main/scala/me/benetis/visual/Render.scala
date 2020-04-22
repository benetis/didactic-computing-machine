package me.benetis.visual

import me.benetis.initial_states.RandomState
import me.benetis.{GameOfLifeRules, LifeState, Point}
import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, document, html}
import scala.util.Random
import zio.clock.Clock
import zio.{IO, Ref, Schedule, Task, UIO, ZIO}
import zio.duration._
object Render {

  val scale = 20

  val rng = new Random()

  val canvas: html.Canvas =
    document.querySelector("#canvas").asInstanceOf[html.Canvas]

  val browserWidth: Int = canvas.parentElement.clientWidth
  val browserHeight: Int = canvas.parentElement.clientHeight

  canvas.width = browserWidth
  canvas.height = browserHeight

  val program: ZIO[Clock, Throwable, Unit] = {

    val renderer = canvas
      .getContext("2d")
      .asInstanceOf[dom.CanvasRenderingContext2D]

    for {
      ref <- Ref.make(RandomState.randomState())
      _ <- (render(renderer, ref) *> updateState(ref) *> UIO(
        dom.console.log("tick")
      )).repeat(Schedule.fixed(1.second)).forever
    } yield ()
  }

  def updateState(ref: Ref[LifeState]): ZIO[Any, Nothing, Unit] =
    for {
      _ <- ref.update(state => GameOfLifeRules.nextState(state))
    } yield ()

  def render(renderer: CanvasRenderingContext2D,
             ref: Ref[LifeState]): ZIO[Any, Nothing, Unit] = {

//    val minX = state.minBy(_.x).x
//    val maxX = state.maxBy(_.x).x
//
//    val minY = state.minBy(_.y).y
//    val maxY = state.maxBy(_.y).y
//
//    val rows = Range.inclusive(minY, maxY)
//    val columns = Range.inclusive(minX, maxX)
//
//    val emptyMatrix: Seq[IndexedSeq[Point]] =
//      rows.map(r => columns.map(c => Point(c, r)))
    for {
      _ <- clearCanvas(renderer)
      state <- ref.get
      _ = state.foreach(p => renderPoint(renderer, p))
    } yield ()
  }

  private def clearCanvas(renderer: CanvasRenderingContext2D): UIO[Unit] = UIO {
    renderer.fillStyle = "black"
    renderer.fillRect(0, 0, browserWidth, browserHeight)
  }

  private def renderPoint(renderer: CanvasRenderingContext2D,
                          point: Point): Unit = {

    val colors = Vector("red", "yellow")

    val randomColor: String = colors(rng.nextInt(colors.length))
    renderer.fillStyle = randomColor
    renderer.fillRect(point.x * scale, point.y * scale, scale, scale)
  }

}
