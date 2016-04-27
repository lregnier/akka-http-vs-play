package com.whiteprompt.common

import kamon.Kamon

trait KamonHandler {
  Kamon.start()

  def stopKamon() = {
    Kamon.shutdown()
  }
}