package ca.ellanVannin.memoizationProblem

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask
import akka.routing.RoundRobinPool
import ca.ellanVannin.memoizationProblem.actors.MemoizationActor.NB_OF_ACTORS
import ca.ellanVannin.memoizationProblem.actors._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Properties

/**
  * Created by Chris on 2017-05-20.
  */
object BlogPostMainClass {
  val NO_MEMO = "NO_MEMO"
  val BAD = "BAD"
  val CONCURRENT_HASHMAP = "CONCURRENT_HASHMAP"
  val SINGLETON_ACTOR = "SINGLETON_ACTOR"

  implicit val TIMEOUT_DURATION: FiniteDuration = 1 minute
  implicit val TIMEOUT: Timeout = Timeout(TIMEOUT_DURATION)

  def main(args: Array[String]): Unit = {
    if (args.length > 0) {
      val system = ActorSystem("MemoizationProblemWithScalaz")

      val actor: ActorRef = args(0) match {
        case NO_MEMO => system.actorOf(NoMemoizationActor.PROPS.withRouter(RoundRobinPool(NB_OF_ACTORS)), "NoMemoizationActor")
        case BAD => system.actorOf(MemoizationActorGoneWrong.PROPS.withRouter(RoundRobinPool(NB_OF_ACTORS)), "MemoizationActorWrong")
        case CONCURRENT_HASHMAP => system.actorOf(MemoizationActorWithConcurrentHashMap.PROPS.withRouter(RoundRobinPool(NB_OF_ACTORS)), "MemoizationActorBetter")
        case SINGLETON_ACTOR =>
          val memoActor = system.actorOf(MemoActor.PROPS, "MemoActor")
          system.actorOf(MemoizationActorWithProperSingletonActor.PROPS(memoActor).withRouter(RoundRobinPool(NB_OF_ACTORS)), "MemoizationActorCorrect")
        case _ => system.actorOf(Props[DisplayHelpActor], "DisplayHelpActor")
      }

      val BEGIN = System.currentTimeMillis()

      val calculations = Future.sequence {
        (0 until 2 * NB_OF_ACTORS).map {
          i =>
            (actor ? s"Message $i").asInstanceOf[Future[String]]
        }
      }

      val result = Await.result(calculations, TIMEOUT_DURATION).foldLeft(new StringBuffer()) {
        (sb, str) => sb.append(str).append(Properties.lineSeparator)
      }

      val END = System.currentTimeMillis()

      System.out.println(
        s"""Complete result took ${END - BEGIN} ms.
           |Result String is:
           |$result""".stripMargin)
      System.exit(
        0)
    } else {
      displayHelp()
    }
  }

  def displayHelp(): Unit = {
    System.err.println(s"Please call the program with either '$NO_MEMO', '$BAD', '$CONCURRENT_HASHMAP' or '$SINGLETON_ACTOR'")
  }
}

class DisplayHelpActor extends Actor {
  override def receive: Receive = {
    case _ => BlogPostMainClass.displayHelp()
  }
}