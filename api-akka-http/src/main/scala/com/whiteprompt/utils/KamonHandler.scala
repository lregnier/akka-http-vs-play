package com.whiteprompt.utils

import kamon.Kamon

trait KamonHandler {
  Kamon.start()

  def stopKamon() = {
    Kamon.shutdown()
  }
}
