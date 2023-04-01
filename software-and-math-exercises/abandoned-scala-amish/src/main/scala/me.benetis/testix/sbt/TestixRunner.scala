package `me.benetis`.testix.sbt

import sbt.testing._

final class TestixRunner(args: Array[String], remoteArgs: Array[String], loader: ClassLoader) extends Runner {
  def tasks(taskDefs: Array[TaskDef]): Array[Task] = 
    taskDefs.toList.map(d => SbtTask(d, loader)).toArray

  def done(): String = "runner done"
  
  def remoteArgs(): Array[String] = remoteArgs
  
  def args(): Array[String] = args
  
}

