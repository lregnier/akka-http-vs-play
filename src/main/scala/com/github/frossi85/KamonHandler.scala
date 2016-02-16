package com.github.frossi85

import kamon.Kamon

trait KamonHandler {
  Kamon.start()

  def stopKamon() = {
    Kamon.shutdown()
  }
}
