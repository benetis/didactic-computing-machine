package `me.benetis`.testix.sbt

import sbt.testing._
import scala.concurrent._
import scala.concurrent
import scala.concurrent.duration._
import java.util.concurrent.Future
import ExecutionContext.Implicits.global

final case class SbtTask(_taskDef: TaskDef, loader: ClassLoader) extends Task {
  def tags(): Array[String] = ???
  
  def execute(eventHandler: EventHandler, loggers: Array[Logger]): Array[Task] = {
    //reflection
    Await.result(concurrent.Future { Thread.sleep(10000) }, Duration.Inf)
    Array()
  }
  
  def taskDef(): TaskDef = _taskDef
  
}
