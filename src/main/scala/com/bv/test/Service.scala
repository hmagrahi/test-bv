package com.bv.test

import akka.actor.Props

class Service(var foos: Int,
              var bars: Int,
              var foobars: Int,
              var balance: Int,
              var robots: Int) {
  def mineFoo(): Unit = foos += 1

  def mineBar(): Unit = bars += 1

  def buy(): Unit = if (balance >= 3 && foos >= 6) {
    robots += 1
    balance -= 3
    foos -= 6
  }

  def sell(): Unit = {
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
  }

  def assemble(isSuccess: Boolean): Unit = {
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
  }
}
