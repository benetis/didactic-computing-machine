package me.benetis

import java.util.concurrent.TimeUnit
import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ButtonType}
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.{HBox, Pane, VBox}
import scalafx.scene.paint.Color._
import scalafx.scene.paint.{LinearGradient, Stops}
import scalafx.scene.text.Text
import zio._
import zio.console._
import scalafx.Includes._
import zio.clock.Clock
import zio.duration.Duration
object GUIMain extends JFXApp {

  val initialState = Set(
    Point(0, 1),
    Point(1, 2),
    Point(2, 1),
    Point(4, 1),
    Point(4, 2),
    Point(3, 2)
  )

  val runtime: Runtime[zio.ZEnv] = Runtime.default

  val Button = new Button("Start")

  stage = new PrimaryStage {
    title = "Game of life"
//    scene = View.universe
    scene = new Scene(500, 500) {
      fill = Black
      content = new VBox {
        children = Seq(View.cellGroup)
      }
    }
  }

  def renderState(ref: Ref[LifeState]): ZIO[Clock, Nothing, Unit] = {

    val render = for {
      state <- ref.get
      newState <- GameOfLifeRules
        .nextState(state)
      _ <- UIO(View.renderCells(newState))
      _ <- ref.update(_ => newState)
    } yield ()

    println("WTF")

    render *> UIO(Platform.runLater(new Runnable {
      override def run(): Unit = Thread.sleep(1000)
    })) *> renderState(ref)
  }

  val update: ZIO[Clock with Console, Nothing, Unit] = for {
    _ <- console.putStrLn("Button click")
    ref <- Ref.make(initialState)
    _ <- renderState(ref)
  } yield ()

  runtime.unsafeRun(update)

}
