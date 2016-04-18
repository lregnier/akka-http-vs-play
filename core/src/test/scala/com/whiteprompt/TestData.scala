package com.whiteprompt

import com.whiteprompt.domain.TaskEntity
import com.whiteprompt.persistence.TaskRepository

import scala.collection.mutable
import scala.concurrent.ExecutionContext

trait TestData {

  implicit val context: ExecutionContext

  val taskEntity1 = TaskEntity(1L, "Foo name", "Foo description")
  val taskEntity2 = TaskEntity(2L, "Bar name", "Bar description")

  val nonExistentTaskId = 1234L

  val taskRepository = new TaskRepository {
    implicit lazy val ec = context
    val store = new mutable.HashMap[Long, TaskEntity]()

    def init(): Unit = {
      store += taskEntity1.id -> taskEntity1
      store += taskEntity2.id -> taskEntity2
    }

    def clear(): Unit = {
      store.empty
    }

    def size(): Int = store.size
  }

}