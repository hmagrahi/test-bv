package com.bv

package object test {
  trait Action

  object Action {
    case object MineFoo extends Action

    case object MineBar extends Action

    case object Assemble extends Action

    case object Sell extends Action

    case object Buy extends Action

    case object End extends Action

    case object Start extends Action
  }

  trait Task

  object Task {
    case object Init extends Task

    case class Mining(m: String, activity: Option[Action]) extends Task

    case class Assembling(isSuccess: Boolean, activity: Option[Action]) extends Task

    case class Selling(activity: Option[Action]) extends Task

    case class Buying(activity: Option[Action]) extends Task

  }
}
