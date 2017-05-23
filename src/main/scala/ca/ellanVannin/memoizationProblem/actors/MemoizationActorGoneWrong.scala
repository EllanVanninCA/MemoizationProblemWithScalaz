package ca.ellanVannin.memoizationProblem.actors

import akka.actor.Props

import scalaz.Memo

/**
  * Created by Chris on 2017-05-20.
  */
class MemoizationActorGoneWrong extends MemoizationActor {
  override def useMemo(number: Int): String = MemoizationActorGoneWrong.memo(number)
}

object MemoizationActorGoneWrong {
  val PROPS: Props = Props[MemoizationActorGoneWrong]

  val memo: Int => String = Memo.mutableHashMapMemo(MemoizationActor.myLongRunningMethod)
}
