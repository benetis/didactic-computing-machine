package me.benetis.visual

import java.util.concurrent.TimeUnit
import me.benetis.initial_states.RandomState
import me.benetis.{GameOfLifeRules, LifeState, Point}
import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, document, html}
import scala.util.Random
import zio.clock.Clock
import zio.{IO, Queue, Ref, Schedule, Task, UIO, ZIO}
import zio.duration._

object Render {
  case class RenderConfig(scale: Int,
                          blobSize: Int,
                          browserWidth: Double,
                          browserHeight: Double,
                          speed: Duration)

  val config =
    RenderConfig(
      10,
      10,
      dom.window.innerWidth,
      dom.window.innerHeight,
      1.second
    )

  val rng = new Random()

  val program: ZIO[Clock, Throwable, Unit] = {
    for {
      renderer <- prepareScreen(config)
      stateQueue <- Queue.bounded[LifeState](10)
      f1 <- generateState(RandomState.randomCenter(config), stateQueue).fork
      f2 <- render(renderer, config, stateQueue).fork

      _ <- reportQueueSize(stateQueue).repeat(Schedule.fixed(1.second)).fork

      _ <- f1.join
      _ <- f2.join
    } yield ()
  }

  private def reportQueueSize(queue: Queue[LifeState]) = {
    for {
      size <- queue.size
      _ <- UIO(dom.console.log(s"queue size: $size"))
    } yield ()
  }

  private def render(renderer: CanvasRenderingContext2D,
                     config: RenderConfig,
                     queue: Queue[LifeState]): ZIO[Clock, Nothing, Unit] = {

    def loop(): ZIO[Clock, Nothing, Unit] =
      for {
        state <- queue.poll
        next <- state match {
          case Some(value) =>
            clearCanvas(renderer, config) *>
              UIO(value.foreach(p => renderPoint(renderer, config, p))) *> ZIO
              .sleep(config.speed) *> loop()
          case None => loop()
        }
      } yield next

    loop()
  }

  private def generateState(
    initialState: LifeState,
    queue: Queue[LifeState]
  ): ZIO[Any, Nothing, Unit] = {

    def loop(state: LifeState): UIO[Unit] =
      queue.offer(state) *> loop(GameOfLifeRules.nextState(state))

    loop(initialState)
  }

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
