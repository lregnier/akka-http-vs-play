package com.whiteprompt

import java.util.UUID

import com.whiteprompt.domain.TaskEntity
import com.whiteprompt.persistence.TaskRepository

import scala.collection.mutable
import scala.concurrent.ExecutionContext

trait TestData {

  val taskEntity1 = TaskEntity(UUID.fromString("c698cafa-de48-428d-a13c-949ab893384f"), "Foo name", "Foo description")
  val taskEntity2 = TaskEntity(UUID.fromString("f92bd520-758f-46ff-b3b8-16c503e08777"), "Bar name", "Bar description")

  val nonExistentTaskId = UUID.fromString("cc5909cc-711f-4a0e-bb29-4109cf0f899d")

  def taskRepository()(implicit context: ExecutionContext) = new TaskRepository {
    implicit lazy val ec = context
    val store = new mutable.HashMap[UUID, TaskEntity]()

    def init(): Unit = {
      store += taskEntity1.id -> taskEntity1
      store += taskEntity2.id -> taskEntity2
    }

    init()
  }

}