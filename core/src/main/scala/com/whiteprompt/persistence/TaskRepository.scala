package com.whiteprompt.persistence

import java.util.UUID

import com.whiteprompt.domain.Task

import scala.collection.mutable
import scala.concurrent.{Future, ExecutionContext}

class TaskRepository(implicit val executionContext: ExecutionContext) extends Repository[Task] with CRUDOps[Task] {
  override val store = new mutable.HashMap[Long, Task]()

  def list(): Future[Seq[Task]] = Future {
    store.values.toSeq.sortWith(_.id < _.id)
  }
}

object TaskRepository {
  def apply()(implicit executionContext: ExecutionContext): TaskRepository = new TaskRepository()
}