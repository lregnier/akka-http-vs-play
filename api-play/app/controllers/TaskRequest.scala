package controllers

import com.whiteprompt.domain.Task

case class TaskRequest(name: String, description: String) extends Task

