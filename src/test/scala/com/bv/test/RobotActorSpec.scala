package com.bv.test

import akka.testkit.TestKit
import akka.actor.{ActorSystem, Props}
import akka.testkit.ImplicitSender
import com.bv.test.Action.{Assemble, Buy, MineBar, MineFoo, Sell, Start}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import com.bv.test.Task.{Assembling, Buying, Mining, Selling}

import scala.concurrent.duration.DurationInt


class RobotActorSpec()
  extends TestKit(ActorSystem("robot-actor-spec"))
    with ImplicitSender
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A robot actor" must {

    "mine foo if he receive Start or MineFoo" in {
      val robot = system.actorOf(Props[Robot]())
      robot ! Start
      expectMsg(Mining("foo", None))
      robot ! MineFoo
      expectMsg(Mining("foo", Some(MineFoo)))
    }

    "mine bar if he receive MineBar" in {
      val robot = system.actorOf(Props[Robot]())
      robot ! MineBar
      expectMsg(Mining("bar", None))
    }

    "Assemble foo and bar if he receive Assemble" in {
      val robot = system.actorOf(Props[Robot]())
      robot ! Assemble
      expectMsgType[Assembling]
    }

    "Buy a robot if he receive buying call" in {
      val robot = system.actorOf(Props[Robot]())
      robot ! Buy
      expectMsg(Buying(None))
    }

    "Sell foobars if he receive Sell" in {
      val robot = system.actorOf(Props[Robot]())
      robot ! Sell
      expectMsg(11.seconds, Selling(None))
    }

  }
}
