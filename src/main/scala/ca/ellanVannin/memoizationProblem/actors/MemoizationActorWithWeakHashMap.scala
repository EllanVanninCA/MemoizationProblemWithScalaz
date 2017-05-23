package ca.ellanVannin.memoizationProblem.actors
package ca.ellanVannin.memoizationProblem.actors

import java.util
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

import akka.actor.Props

import scala.collection.JavaConverters._
import scalaz.Memo

/**
  * Created by Chris on 2017-05-23.
  */
class MemoizationActorWithWeakHashMap extends MemoizationActor {
  override def useMemo(number: Int): String = MemoizationActorWithConcurrentHashMap.improvedMemo(number)
}

object MemoizationActorWithWeakHashMap {
  def PROPS: Props = Props[MemoizationActorWithWeakHashMap]

  val concurrentMap = Collections.synchronizedMap(new util.WeakHashMap[Int, String]()).asScala

  val improvedMemo: Int => String = Memo.mutableMapMemo(concurrentMap)(MemoizationActor.myLongRunningMethod)
}
