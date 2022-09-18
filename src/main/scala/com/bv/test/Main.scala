package com.bv.test

import akka.actor.{Actor, ActorSystem, Props}
import com.bv.test.Action.Start
import com.bv.test.FSM.next
import com.bv.test.Task._

import scala.concurrent.Await
import scala.concurrent.duration.Duration


class Factory(service: Service) extends Actor with akka.actor.ActorLogging {

  override def receive: Receive = { v => {
    log.info(s"Foos ${service.foos}, bars ${service.bars}, foobars: ${service.foobars}, balance: ${service.balance}, robots: ${service.robots}")
    v match {
      case Init =>
        log.info(s"Init ${service.robots}")
        val robot1 = context.actorOf(Props[Robot](), name = "robot-1")
        val robot2 = context.actorOf(Props[Robot](), name = "robot-2")
        robot1 ! Start
        robot2 ! Start

      case Mining(m: String, oldActivity: Option[Action]) =>
        log.info(s"Mining $m")
        m match {
          case "foo" => service.mineFoo()
          case "bar" => service.mineBar()
          case _ => ()
        }
        val nextMove: Action = next(oldActivity, service.foos, service.bars, service.foobars, service.balance, service.robots)
        sender() ! nextMove
      case Assembling(isSuccess: Boolean, activity: Option[Action]) =>
        log.info(s"Assembling $isSuccess")
        service.assemble(isSuccess)
        val nextMove: Action = next(activity, service.foos, service.bars, service.foobars, service.balance, service.robots)
        sender() ! nextMove
      case Selling(activity: Option[Action]) =>
        log.info(s"Selling from $service.foobars")
        service.sell()
        val nextMove: Action = next(activity, service.foos, service.bars, service.foobars, service.balance, service.robots)
        sender() ! nextMove
      case Buying(activity: Option[Action]) =>
        log.info("Buying a robot")
        if (service.balance >= 3 && service.foos >= 6) {
          service.buy()
          val name = s"robot-${service.robots + 1}"
          val childRef = context.actorOf(Props[Robot](), name)
          childRef ! Start
        }
        if (service.robots >= 30) {
          context.system.terminate()
        }
        val nextMove: Action = next(activity, service.foos, service.bars, service.foobars, service.balance, service.robots)
        sender() ! nextMove
    }
  }
  }
}

object Main extends App {
  val system = ActorSystem("test")
  var foos: Int = 0
  var bars: Int = 0
  var foobars: Int = 0
  var balance: Int = 0
  var robots: Int = 2
  val service = new Service(foos, bars, foobars, balance, robots)
  val factory = system.actorOf(Props(classOf[Factory],service), name = "service-actor")
  factory ! Init
  Await.ready(system.whenTerminated, Duration(100, "minutes"))
}
