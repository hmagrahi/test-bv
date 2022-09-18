package com.bv.test

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.bv.test.Action._
import com.bv.test.Main.{balance, bars, foobars, foos, robots}
import com.bv.test.Task.{Assembling, Buying, Mining, Selling}
import org.scalatest.{BeforeAndAfterAll, color}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration.DurationInt


class ServiceSpec()
  extends AnyWordSpecLike
    with Matchers {


  "A service actor" must {

    "add a foo if he receive Mining task" in {
      val data = prepareStores()
      val service = new Service(data.foos, data.bars, data.foobars, data.balance, data.robots)

      service.mineFoo()
      service.foos shouldBe 1
    }

    "mine bar if he receive MineBar" in {
      val data = prepareStores()
      val service = new Service(data.foos, data.bars, data.foobars, data.balance, data.robots)

      service.mineBar()
      service.bars shouldBe 1
    }

    "Assemble foo and bar if he receive a successful Assemble" in {
      val data = prepareStores()
      data.foos = 1
      data.bars = 1
      val service = new Service(data.foos, data.bars, data.foobars, data.balance, data.robots)

      service.assemble(true)

      service.foobars shouldBe 1
      service.bars shouldBe 0
      service.foos shouldBe 0
    }

    " not Assemble foo and bar and keep only a bar if he receive a false Assemble" in {
      val data = prepareStores()
      data.foos = 1
      data.bars = 1
      val service = new Service(data.foos, data.bars, data.foobars, data.balance, data.robots)

      service.assemble(false)

      service.foobars shouldBe 0
      service.bars shouldBe 1
      service.foos shouldBe 0
    }

    "Buy a robot if he receive buying call" in {
      val data = prepareStores()
      data.foos = 6
      data.balance = 3
      val service = new Service(data.foos, data.bars, data.foobars, data.balance, data.robots)

      service.buy()
      service.balance shouldBe 0
      service.foos shouldBe 0
      service.robots shouldBe 1
    }

    "Sell foobars if he receive Sell" in {
      val data = prepareStores()
      data.foobars = 10
      val service = new Service(data.foos, data.bars, data.foobars, data.balance, data.robots)
      service.sell()

      service.foobars shouldBe 5
      service.balance shouldBe 5
    }
  }

  private def prepareStores() = new {
    var robots = 0
    var foobars = 0
    var bars = 0
    var foos = 0
    var balance = 0
  }
}
