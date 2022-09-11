package com.bv.test

import com.bv.test.Action._

object FSM {
  def next(oldActivity: Option[Action],
           foosSize: Int,
           barsSize: Int,
           foobarsSize: Int,
           b: Int,
           robotSize: Int): Action = {
    if (robotSize >= 30) {
      End
    } else
      oldActivity match {
        case None if foosSize == 0 =>
          MineFoo
        case None if barsSize == 0 =>
          MineBar
        case None =>
          Assemble
        case Some(MineFoo) if barsSize == 0 =>
          MineBar
        case Some(MineBar) if barsSize == 0 =>
          MineBar
        case Some(MineBar) | Some(MineFoo) if foosSize < 6 =>
          MineFoo
        case Some(MineBar) | Some(MineFoo) =>
          Assemble
        case Some(Assemble) if foosSize >= 6 && b >= 3 && robotSize <= 30 =>
          Buy
        case Some(Assemble) if foobarsSize != 0 =>
          Sell
        case Some(Assemble) if robotSize >= 30 =>
          End
        case Some(Assemble) if barsSize == 0 =>
          MineBar
        case Some(Assemble) if foosSize < 6 =>
          MineFoo
        case Some(Assemble) =>
          Assemble
        case Some(Sell) if foobarsSize == 0 =>
          Sell
        case Some(Sell) if robotSize >= 30 =>
          End
        case Some(Sell) if foosSize < 6 =>
          MineFoo
        case Some(Sell) if foosSize >= 6 && b >= 3 && robotSize <= 30 =>
          Buy
        case Some(Sell) if barsSize == 0 =>
          MineBar
        case Some(Sell) if barsSize == 0 =>
          MineFoo
        case Some(Sell) =>
          Buy
        case Some(Buy) if robotSize >= 30 => End
        case Some(Sell) if foosSize >= 6 && b >= 3 && robotSize <= 30 =>
          Buy
        case Some(Buy) if foosSize < 6 =>
          MineFoo
        case Some(Buy) if foobarsSize != 0 =>
          Sell
        case Some(Buy) if barsSize == 0 =>
          MineBar
        case Some(Buy) if barsSize == 0 =>
          MineFoo
        case Some(Buy) =>
          Assemble
        case _ => End
      }
  }

}
