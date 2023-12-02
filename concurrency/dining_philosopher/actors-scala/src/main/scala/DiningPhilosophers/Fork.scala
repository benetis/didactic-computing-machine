package DiningPhilosophers

import DiningPhilosophers.ForkMessages._
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

class Fork extends Actor with ActorLogging {

  /* Fork can be in two states: available or in use */

  def available: Receive = {
    case Take =>
      log.info(s"Fork ${self.path.name} by ${sender.path.name}")
      sender ! ForkTaken
      context.become(inUse(sender))
  }

  def inUse(philosopher: ActorRef): Receive = {
    case Take =>
      log.info(s"Fork ${self.path.name} already being used by ${philosopher.path.name}")
      sender ! ForkBeingUsed
    case Put =>
      log.info(s"Fork ${self.path.name} put down by ${sender.path.name}")
      sender ! Put
      context.become(available)
  }

  def receive = available

}