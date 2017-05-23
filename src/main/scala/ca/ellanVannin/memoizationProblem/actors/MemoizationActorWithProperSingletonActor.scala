package ca.ellanVannin.memoizationProblem.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import ca.ellanVannin.memoizationProblem.BlogPostMainClass

import scala.concurrent.Await
import scalaz.Memo

/**
  * Created by Chris on 2017-05-21.
  */
class MemoizationActorWithProperSingletonActor(memoActor: ActorRef) extends MemoizationActor {
  override def useMemo(number: Int): String = {
    implicit val TIMEOUT = BlogPostMainClass.TIMEOUT

    val callMemo = memoActor ? number

    //I wouldn't use Await here in the real world but wanted to keep the same method signature as for previous examples.
    Await.result(callMemo, BlogPostMainClass.TIMEOUT_DURATION).asInstanceOf[String]
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
