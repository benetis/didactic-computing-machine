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
  case class RenderConfig(scale: Int,
                          blobSize: Int,
                          browserWidth: Double,
                          browserHeight: Double)

  val config =
    RenderConfig(10, 20, dom.window.innerWidth, dom.window.innerHeight)

  val rng = new Random()

  val program: ZIO[Clock, Throwable, Unit] = {
    for {
      renderer <- prepareScreen(config)
      refState <- Ref.make(RandomState.randomCenter(config))
      _ <- (render(renderer, config, refState) *> updateState(refState) *> UIO(
        dom.console.log("tick")
      )).repeat(Schedule.fixed(1.second)).forever
    } yield ()
  }

  private def render(renderer: CanvasRenderingContext2D,
                     config: RenderConfig,
                     ref: Ref[LifeState]): ZIO[Any, Nothing, Unit] = {
    for {
      _ <- clearCanvas(renderer, config)
      state <- ref.get
      _ = state.foreach(p => renderPoint(renderer, config, p))
    } yield ()
  }

  private def updateState(ref: Ref[LifeState]): ZIO[Any, Nothing, Unit] =
    for {
      _ <- ref.update(state => GameOfLifeRules.nextState(state))
    } yield ()

  private def clearCanvas(renderer: CanvasRenderingContext2D,
                          config: RenderConfig): UIO[Unit] = UIO {
    renderer.fillStyle = "black"
    renderer.fillRect(0, 0, config.browserWidth, config.browserHeight)
  }

  private def renderPoint(renderer: CanvasRenderingContext2D,
                          config: RenderConfig,
                          point: Point): Unit = {

    val colors = Vector("red", "yellow")

    val randomColor: String = colors(rng.nextInt(colors.length))
    renderer.fillStyle = randomColor
    renderer.fillRect(
      point.x * config.scale,
      point.y * config.scale,
      config.scale,
      config.scale
    )
  }

  private def prepareScreen(
    config: RenderConfig
  ): UIO[CanvasRenderingContext2D] = UIO {
    val canvas: html.Canvas =
      document.querySelector("#canvas").asInstanceOf[html.Canvas]

    canvas.width = config.browserWidth.toInt
    canvas.height = config.browserHeight.toInt

    val renderer = canvas
      .getContext("2d")
      .asInstanceOf[dom.CanvasRenderingContext2D]

    renderer
  }

}
