package com.bv.test

import akka.actor.{Actor, ActorSystem, Props}
import com.bv.test.Action.{Assemble, Buy, End, MineBar, MineFoo, Sell, Start}
import com.bv.test.FSM.next
import com.bv.test.Task._

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt}
import scala.util.Random


class Robot extends Actor {
  var oldActivity: Option[Action] = None
  var nextActivity: Option[Action] = None

  def init(newActivity: Action): Unit = {
    oldActivity match {
      case Some(oa) if oa == newActivity =>
        oldActivity = Some(newActivity)
        nextActivity = Some(newActivity)
      case Some(oa) =>
        Thread.sleep(5000)
        nextActivity = Some(newActivity)
        oldActivity = Some(oa)
      case None =>
        Thread.sleep(2000)
        oldActivity = None
        nextActivity = Some(newActivity)
    }
  }

  override def receive: Receive = {
    case End =>
      context.system.terminate()
    case Start | MineFoo =>
      oldActivity = nextActivity
      nextActivity = Some(MineFoo)
      Thread.sleep(2.seconds.toMillis)
      sender() ! Mining("foo", oldActivity)
    case MineBar =>
      oldActivity = nextActivity
      nextActivity = Some(MineBar)
      Thread.sleep(Random.between(500, 2000))
      sender() ! Mining("bar", oldActivity)
    case Assemble =>
      oldActivity = nextActivity
      nextActivity = Some(Assemble)
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
      Thread.sleep(10.seconds.toMillis)
      sender() ! Selling(oldActivity)
    case Buy =>
      oldActivity = nextActivity
      nextActivity = Some(Buy)
      sender() ! Buying(oldActivity)
  }
}

class Service extends Actor with akka.actor.ActorLogging {
  var foos: Int = 0
  var bars: Int = 0
  var foobars: Int = 0
  var balance: Int = 0
  var robots: Int = 2

  override def receive: Receive = { v => {
    log.info(s"Foos $foos, bars $bars, foobars: $foobars, balance: $balance, robots: $robots")
    v match {
      case Init =>
        log.info(s"Init $robots")
        val robot1 = context.actorOf(Props[Robot](), name = "robot1")
        val robot2 = context.actorOf(Props[Robot](), name = "robot2")
        robot1 ! Start
        robot2 ! Start

      case Mining(m: String, oldActivity: Option[Action]) =>
        log.info(s"Mining $m")
        m match {
          case "foo" => foos += 1
          case "bar" => bars += 1
          case _ => ()
        }
        val nextMove: Action = next(oldActivity, foos, bars, foobars, balance, robots)
        sender() ! nextMove
      case Assembling(isSuccess: Boolean, activity: Option[Action]) =>
        log.info(s"Assembling $isSuccess")
        if (isSuccess) {
          if (foos > 0 && bars > 0) {
            foobars += 1
            foos -= 1
            bars -= 1
          }
        } else if (foos > 0) {
          foos -= 1
        } else {
          ()
        }
        val nextMove: Action = next(activity, foos, bars, foobars, balance, robots)
        sender() ! nextMove
      case Selling(activity: Option[Action]) =>
        log.info(s"Selling from $foobars")
        if (foobars >= 5) {
          foobars -= 5
          balance += 5
        } else if (foobars >= 4) {
          foobars -= 4
          balance += 4
        } else if (foobars >= 3) {
          foobars -= 3
          balance += 3
        } else if (foobars >= 2) {
          foobars -= 2
          balance += 2
        } else if (foobars >= 1) {
          foobars -= 1
          balance += 1
        } else foobars -= 0
        val nextMove: Action = next(activity, foos, bars, foobars, balance, robots)
        sender() ! nextMove
      case Buying(activity: Option[Action]) =>
        log.info("Buying a robot")
        if (balance >= 3 && foos >= 6) {
          robots += 1
          balance -= 3
          foos -= 6
          val name = s"robot-${robots + 1}"
          val childRef = context.actorOf(Props[Robot](), name)
          childRef ! Start
        }
        if (robots >= 30) {
          context.system.terminate()
        }
        val nextMove: Action = next(activity, foos, bars, foobars, balance, robots)
        sender() ! nextMove
    }
  }
  }
}

object Main extends App {
  val system = ActorSystem("test")

  val service = system.actorOf(Props[Service], name = "service-actor")
  service ! Init
  Await.ready(system.whenTerminated, Duration(100, "minutes"))
}
