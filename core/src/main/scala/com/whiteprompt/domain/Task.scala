package com.whiteprompt.domain

import java.util.UUID

import com.whiteprompt.persistence.UUIDEntity

trait Task {
  val name: String
  val description: String
}

case class TaskEntity(id: UUID, name: String, description: String) extends UUIDEntity with Task
