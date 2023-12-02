package DiningPhilosophers

/* Code learned/taken from:
 - https://github.com/42ways/akka-dining-philosophers/blob/master/src/main/scala/ws/fortytwo/experimental/akka/philosophers/Philosopher.scala
 - https://github.com/akka/akka-quickstart-scala.g8
 */

import DiningPhilosophers.PhilosopherMessages.Think
import akka.actor.{ActorSystem, Props}
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {

  val nPhilosophers = 5

  val system: ActorSystem = ActorSystem("DiningPhilosophers")


  val forks = (0 to nPhilosophers).map(ith => system.actorOf(Props[Fork], s"Fork.$ith"))
  val philosophers = (0 to nPhilosophers).map(ith => system.actorOf(
    Props(classOf[Philosopher], forks(ith), forks((ith + 1) % 5)), s"Philosopher.$ith"
  ))

  philosophers.foreach( _ ! Think)

}
