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
  val BETTER = "BETTER"

  implicit val TIMEOUT_DURATION = 1 minute
  implicit val TIMEOUT = Timeout(TIMEOUT_DURATION)

  def main(args: Array[String]): Unit = {
    if (args.length > 0) {
      val system = ActorSystem("MemoizationProblemWithScalaz")

      val actor: ActorRef = args(0) match {
        case NO_MEMO => system.actorOf(NoMemoizationActor.PROPS.withRouter(RoundRobinPool(NB_OF_ACTORS)), "NoMemoizationActor")
        case BAD => system.actorOf(MemoizationActorWrong.PROPS.withRouter(RoundRobinPool(NB_OF_ACTORS)), "MemoizationActorWrong")
        case BETTER => system.actorOf(MemoizationActorBetter.PROPS.withRouter(RoundRobinPool(NB_OF_ACTORS)), "MemoizationActorBetter")
        case _ => system.actorOf(Props[DisplayHelpActor], "DisplayHelpActor")
      }

      val BEGIN = System.currentTimeMillis()

      val calculations = Future.sequence {
        (0 until NB_OF_ACTORS).map {
          i =>
            (actor ? s"Message $i") match {
              case x: Future[String] => x
            }
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
    System.err.println(s"Please call the program with either '$NO_MEMO', '$BAD' or '$BETTER'")
  }
}

class DisplayHelpActor extends Actor {
  override def receive = {
    case _ => BlogPostMainClass.displayHelp()
  }
}