package com.whiteprompt

import kamon.Kamon

trait KamonHandler {
  Kamon.start()

  def stopKamon() = {
    Kamon.shutdown()
  }
}
