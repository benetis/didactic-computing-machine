package me.benetis
import me.benetis.visual.Render
import org.scalajs.dom
import org.scalajs.dom.document
import zio._
import zio.console._
import zio.clock.Clock
import zio.duration.Duration

object Main extends App {

//  def appendPar(targetNode: dom.Node, text: String): Unit = {
//    val parNode = document.createElement("p")
//    val textNode = document.createTextNode(text)
//    parNode.appendChild(textNode)
//    targetNode.appendChild(parNode)
//  }
//
//  def appendContinuous(): ZIO[Clock, Nothing, Nothing] = {
//    UIO(appendPar(document.body, "Hello World!")) *> ZIO.sleep(
//      Duration(1, TimeUnit.SECONDS)
//    ) *> appendContinuous()
//  }

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {

    val program = for {
      _ <- Render.program
    } yield 0

    val withErrors = program
      .catchAll(e => console.putStrLn(s"Application failed with $e").as(1))

    val env = Clock.live ++ Console.live

    withErrors.provideLayer(env)

  }

}
