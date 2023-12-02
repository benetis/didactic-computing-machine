package DiningPhilosophers

import DiningPhilosophers.ForkMessages._
import DiningPhilosophers.PhilosopherMessages._
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.DurationInt
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global

class Philosopher(val leftFork: ActorRef, val rightFork: ActorRef) extends Actor with ActorLogging {

  def name = self.path.name

  private val eatingTime = 2500.millis
  private val thinkingTime = 5000.millis
  private val retryTime = 10.millis


  def thinkFor(duration: FiniteDuration) = {
    context.system.scheduler.scheduleOnce(duration, self, Eat)
    context.become(thinking)
  }

  def thinking: Receive = {
    case Eat =>
      log.info(s"Philosopher ${self.path.name} wants to eat")
      leftFork ! Take
      rightFork ! Take
      context.become(hungry)
  }

  def hungry: Receive = {
    case ForkBeingUsed => handleForkBeingUsed()
    case ForkTaken =>
      log.info(s"Philosopher ${self.path.name} found one fork to be taken by other philosopher")
      context.become(waitingForOtherFork)
  }

  def waitingForOtherFork: Receive = {
    case ForkBeingUsed => handleForkBeingUsed()
    case ForkTaken =>
      log.info(s"Philosopher ${self.path.name} starts to eat")
      context.system.scheduler.scheduleOnce(eatingTime, self, Think)
      context.become(eating)
  }

  def eating: Receive = {
    case Think =>
      log.info(s"Philosopher ${self.path.name} starts to think")
      leftFork ! Put
      rightFork ! Put
      thinkFor(thinkingTime)
  }

  def handleForkBeingUsed(): Unit = {
    log.info(s"Philosopher ${self.path.name} found one fork to be in use")
    /* Put both forks down */
    leftFork ! Put
    rightFork ! Put
    thinkFor(retryTime)
  }

  def receive = {
    case Think =>
      log.info(s"Philosopher ${self.path.name} started thinking")
      thinkFor(thinkingTime)

  }
}
