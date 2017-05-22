package ca.ellanVannin.memoizationProblem.actors

import akka.actor.Props

import scalaz.Memo

/**
  * Created by Chris on 2017-05-20.
  */
class MemoizationActorWrong extends MemoizationActor {
  override def useMemo(number: Int): String = MemoizationActorWrong.memo(number)
}

object MemoizationActorWrong {
  val PROPS = Props[MemoizationActorWrong]

  val memo: Int => String = Memo.mutableHashMapMemo(MemoizationActor.myLongRunningMethod)
}
