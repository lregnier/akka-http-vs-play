package com.whiteprompt.persistence

import java.util.UUID

import com.whiteprompt.domain.TaskEntity

import scala.collection.mutable
import scala.concurrent.{Future, ExecutionContext}

class TaskRepository(implicit val executionContext: ExecutionContext) extends Repository[TaskEntity] with CRUDOps[TaskEntity] {
  override val store = new mutable.HashMap[Long, TaskEntity]()

  def list(): Future[Seq[TaskEntity]] = Future {
    store.values.toSeq.sortWith(_.id < _.id)
  }
}

object TaskRepository {
  def apply()(implicit executionContext: ExecutionContext): TaskRepository = new TaskRepository()
}