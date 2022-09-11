package com.bv.test


import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RobotFSMSpec extends AnyFlatSpec with Matchers {


  "Robot FSM - process" should "mine foos if first" in {
    val data = prepareStores()
    val nextMove = FSM.next(None, data.foos, data.bars, data.foobars, data.balance, data.robots)
    nextMove shouldBe Action.MineFoo
  }

  it should "mine bars if size = 0 if foos is not empty" in {
    val data = prepareStores()
    data.bars = 0
    data.foos = 10
    val nextMove = FSM.next(Some(Action.MineFoo), data.foos, data.bars, data.foobars, data.balance, data.robots)
    nextMove shouldBe Action.MineBar
  }

  it should "assemble if bars are not empty and foos > 6" in {
    val data = prepareStores()
    data.bars = 1
    data.foos = 7
    val nextMove = FSM.next(Some(Action.MineFoo), data.foos, data.bars, data.foobars, data.balance, data.robots)
    nextMove shouldBe Action.Assemble
  }
  it should "do not move if foos is empty" in {
    val data = prepareStores()
    data.foos = 0
    data.bars = 10
    val nextMove = FSM.next(Some(Action.MineFoo), data.foos, data.bars, data.foobars, data.balance, data.robots)
    nextMove shouldBe Action.MineFoo
  }

  it should "do not move if bars is empty" in {
    val data = prepareStores()
    data.foos = 10
    data.bars = 0
    val nextMove = FSM.next(Some(Action.MineBar), data.foos, data.bars, data.foobars, data.balance, data.robots)
    nextMove shouldBe Action.MineBar
  }

  it should "keep mining foos if it is lower than 6" in {
    val data = prepareStores()
    data.foos = 4
    data.bars = 1
    val nextMove = FSM.next(Some(Action.MineFoo), data.foos, data.bars, data.foobars, data.balance, data.robots)
    nextMove shouldBe Action.MineFoo
  }

  it should "keep mining foos from MineBar if it is lower than 6" in {
    val data = prepareStores()
    data.foos = 5
    data.bars = 1
    val nextMove = FSM.next(Some(Action.MineBar), data.foos, data.bars, data.foobars, data.balance, data.robots)
    nextMove shouldBe Action.MineFoo
  }
  it should "buy a robot after assembling" in {
    val data = prepareStores()
    data.foos = 10
    data.bars = 10
    data.foobars = 10
    data.balance = 10
    val nextMove = FSM.next(Some(Action.Assemble), data.foos, data.bars, data.foobars, data.balance, data.robots)
    nextMove shouldBe Action.Buy
  }

  it should "move to end if robots > 30" in {
    val data = prepareStores()
    data.robots = 30
    val nextMove = FSM.next(Some(Action.MineFoo), data.foos, data.bars, data.foobars, data.balance, data.robots)
    nextMove shouldBe Action.End
  }

  private def prepareStores() = new {
    var robots = 0
    var foobars = 0
    var bars = 0
    var foos = 0
    var balance = 0

  }
}

