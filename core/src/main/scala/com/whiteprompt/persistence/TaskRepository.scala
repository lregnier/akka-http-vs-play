package com.whiteprompt.persistence

import java.util.UUID

import com.whiteprompt.domain.TaskEntity

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class TaskRepositoryImpl(implicit val ec: ExecutionContext) extends TaskRepository {
  override val store = new mutable.HashMap[UUID, TaskEntity]()
}

trait TaskRepository extends Repository[UUID, TaskEntity] with CRUDOps[UUID, TaskEntity] {
  def list(): Future[Seq[TaskEntity]] = Future {
    store.values.toSeq
  }
}

object TaskRepository {
  def apply()(implicit executionContext: ExecutionContext): TaskRepository = new TaskRepositoryImpl()
}