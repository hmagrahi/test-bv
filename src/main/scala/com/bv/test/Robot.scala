package com.bv.test

import akka.actor.Actor
import com.bv.test.Action.{Assemble, Buy, End, MineBar, MineFoo, Sell, Start}
import com.bv.test.Task.{Assembling, Buying, Mining, Selling}

import scala.concurrent.duration.DurationInt
import scala.util.Random

class Robot extends Actor with akka.actor.ActorLogging {
  var oldActivity: Option[Action] = None
  var nextActivity: Option[Action] = None

  def init(newActivity: Action): Unit = {
    log.debug(s"Changing activity from $oldActivity to $nextActivity")
    oldActivity match {
      case Some(oa) if oa == newActivity => ()
      case Some(oa) =>
        log.debug("Waiting 5 seconds")
        Thread.sleep(5000)
      case None => ()
    }
  }

  override def receive: Receive = {
    case End =>
      context.system.terminate()
    case Start | MineFoo =>
      oldActivity = nextActivity
      nextActivity = Some(MineFoo)
      init(MineFoo)
      Thread.sleep(2.seconds.toMillis)
      sender() ! Mining("foo", oldActivity)
    case MineBar =>
      oldActivity = nextActivity
      nextActivity = Some(MineBar)
      init(MineBar)
      Thread.sleep(Random.between(500, 2000))
      sender() ! Mining("bar", oldActivity)
    case Assemble =>
      oldActivity = nextActivity
      nextActivity = Some(Assemble)
      init(Assemble)
      Thread.sleep(2.seconds.toMillis)
      val isFinished = (Math.random() * 100) <= 60
      if (isFinished) {
        sender() ! Assembling(isSuccess = true, oldActivity)
      } else {
        sender() ! Assembling(isSuccess = false, oldActivity)
      }
    case Sell =>
      oldActivity = nextActivity
      nextActivity = Some(Sell)
      init(Sell)
      Thread.sleep(10.seconds.toMillis)
      sender() ! Selling(oldActivity)
    case Buy =>
      oldActivity = nextActivity
      nextActivity = Some(Buy)
      init(Buy)
      sender() ! Buying(oldActivity)
  }
}