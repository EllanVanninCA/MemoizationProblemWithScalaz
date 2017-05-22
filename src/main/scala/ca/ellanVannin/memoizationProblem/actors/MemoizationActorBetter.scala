package ca.ellanVannin.memoizationProblem.actors

import java.util.concurrent.ConcurrentHashMap

import akka.actor.Props

import scala.collection.JavaConverters._
import scalaz.Memo

/**
  * Created by Chris on 2017-05-21.
  */
class MemoizationActorBetter extends MemoizationActor {
  override def useMemo(number: Int): String = MemoizationActorBetter.improvedMemo(number)
}

object MemoizationActorBetter {
  def PROPS = Props[MemoizationActorBetter]

  val concurrentMap = new ConcurrentHashMap[Int, String]().asScala

  val improvedMemo: Int => String = Memo.mutableMapMemo(concurrentMap)(MemoizationActor.myLongRunningMethod)
}
