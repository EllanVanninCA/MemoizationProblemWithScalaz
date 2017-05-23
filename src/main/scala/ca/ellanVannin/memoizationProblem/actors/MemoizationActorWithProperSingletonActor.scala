package ca.ellanVannin.memoizationProblem.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration
import scalaz.Memo
import scala.concurrent.duration._

/**
  * Created by Chris on 2017-05-21.
  */
class MemoizationActorWithProperSingletonActor(memoActor: ActorRef) extends MemoizationActor {
  implicit val TIMEOUT_DURATION: FiniteDuration = 1 minute
  implicit val TIMEOUT: Timeout = Timeout(TIMEOUT_DURATION)

  override def useMemo(number: Int): String = {
    val callMemo = memoActor ? number

    //I wouldn't use Await here in the real world but wanted to keep the same method signature as for previous examples.
    Await.result(callMemo, TIMEOUT_DURATION).asInstanceOf[String]
  }
}

object MemoizationActorWithProperSingletonActor {
  def PROPS(actor: ActorRef) = Props(new MemoizationActorWithProperSingletonActor(actor))
}

class MemoActor extends Actor {
  val memo = Memo.mutableHashMapMemo {
    MemoizationActor.myLongRunningMethod
  }

  override def receive: Receive = {
    case myIntegerMessage: Int => sender() ! memo(myIntegerMessage)
  }
}

object MemoActor {
  val PROPS: Props = Props[MemoActor]
}
