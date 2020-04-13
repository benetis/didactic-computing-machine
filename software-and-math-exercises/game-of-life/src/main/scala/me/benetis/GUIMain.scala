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
    Point(3, 2),
    Point(5, 1)
  )

  val runtime: Runtime[zio.ZEnv] = Runtime.default

  val Button = new Button("Start")

  def renderState(ref: Ref[LifeState]): ZIO[Clock, Nothing, Unit] = {

    val render = for {
      state <- ref.get
      newState <- GameOfLifeRules
        .nextState(state)
      _ <- UIO(View.renderCells(newState))
      _ <- ref.update(_ => newState)
    } yield ()

    println("WTF")

    render *> ZIO.sleep(Duration(1, TimeUnit.SECONDS)) *> renderState(ref)
  }

  val update: ZIO[Clock with Console, Nothing, Unit] = for {
    _ <- console.putStrLn("Button click")
    ref <- Ref.make(initialState)
    _ <- renderState(ref)
  } yield ()

  val primaryStage: PrimaryStage = new PrimaryStage {
    title = "Game of life"
    scene = new Scene(500, 500) {
      fill = Black
      content = new VBox {
        children = Seq(View.cellGroup)
      }
    }
  }

  primaryStage.setOnShown(e => {
    runtime.unsafeRunSync(update)
  })

  stage = primaryStage

}
