package DiningPhilosophers

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props }

object PhilosopherMessages {
  case object Eat
  case object Think
}


object ForkMessages {
  case object Take
  case object Put
  case object ForkBeingUsed
  case object ForkTaken
}
