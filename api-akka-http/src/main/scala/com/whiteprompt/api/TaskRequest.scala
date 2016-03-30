package com.whiteprompt.api

import com.whiteprompt.domain.Task

case class TaskRequest(name: String, description: String) extends Task {
  require(name.length >= 3 && name.length <= 25)
}
