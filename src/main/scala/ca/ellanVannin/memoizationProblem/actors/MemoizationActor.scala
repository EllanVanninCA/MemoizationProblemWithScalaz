package ca.ellanVannin.memoizationProblem.actors

import akka.actor.Actor

import scala.util.{Properties, Random}

/**
  * Created by Chris on 2017-05-21.
  */
trait MemoizationActor extends Actor {
  override def receive: Receive = {
    case msg: String =>
      import MemoizationActor._

      val beginningTime = System.currentTimeMillis()

      for (i <- 0 to ITERATIONS)
        useMemo(i)

      val duration = System.currentTimeMillis() - beginningTime

      sender ! s"$msg - processed in $duration ms by actor '${this.self}'${Properties.lineSeparator}"
  }

  def useMemo(number: Int): String
}

object MemoizationActor {
  val ITERATIONS = 100
  val NB_OF_ACTORS = 50

  /**
    * Mock method used as a base for my memo. This method takes 20+ ms to execute which feels like forever when called multiple times.
    *
    * @param number Integer input
    * @return String containing the unit digit. Here, I probably could return a Unit but that'd be the purpose of using a Memo...
    */
  def myLongRunningMethod(number: Int): String = {
    Thread.sleep(20)
    (number % 10).toString
  }
}
