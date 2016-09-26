package com.whiteprompt.domain

import com.whiteprompt.persistence.Entity

trait Task {
  val name: String
  val description: String
}

case class TaskEntity(id: String, name: String, description: String) extends Entity with Task
