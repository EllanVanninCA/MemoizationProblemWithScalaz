package ca.ellanVannin.memoizationProblem.actors

import java.util.concurrent.ConcurrentHashMap

import akka.actor.Props

import scala.collection.JavaConverters._
import scalaz.Memo

/**
  * Created by Chris on 2017-05-21.
  */
class MemoizationActorWithConcurrentHashMap extends MemoizationActor {
  override def useMemo(number: Int): String = MemoizationActorWithConcurrentHashMap.improvedMemo(number)
}

object MemoizationActorWithConcurrentHashMap {
  def PROPS: Props = Props[MemoizationActorWithConcurrentHashMap]

  val concurrentMap: ConcurrentHashMap[Int, String] = new ConcurrentHashMap[Int, String]().asScala

  val improvedMemo: Int => String = Memo.mutableMapMemo(concurrentMap)(MemoizationActor.myLongRunningMethod)
}
