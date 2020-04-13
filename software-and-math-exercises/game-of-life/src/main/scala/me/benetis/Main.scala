package me.benetis
import java.util.concurrent.TimeUnit
import org.scalajs.dom
import org.scalajs.dom.document
import zio._
import zio.clock.Clock
import zio.duration.Duration
object Main extends App {

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    targetNode.appendChild(parNode)
  }

  def appendContinous(): ZIO[Clock, Nothing, Nothing] = {
    UIO(appendPar(document.body, "Hello World")) *> ZIO.sleep(
      Duration(1, TimeUnit.SECONDS)
    ) *> appendContinous()
  }

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    appendContinous().provideLayer(Clock.live).as(0)
  }

}
