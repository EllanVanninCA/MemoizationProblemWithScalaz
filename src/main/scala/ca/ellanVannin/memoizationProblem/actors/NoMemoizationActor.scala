package ca.ellanVannin.memoizationProblem.actors

import akka.actor.Props

/**
  * Created by Chris on 2017-05-21.
  */
class NoMemoizationActor extends MemoizationActor {
  override def useMemo(number: Int): String = MemoizationActor.myLongRunningMethod(number)
}

object NoMemoizationActor {
  val PROPS = Props[NoMemoizationActor]
}
