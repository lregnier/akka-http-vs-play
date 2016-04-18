package com.whiteprompt.persistence

import com.whiteprompt.domain.TaskEntity

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class TaskRepositoryImpl(implicit val ec: ExecutionContext) extends TaskRepository {
  override val store = new mutable.HashMap[Long, TaskEntity]()
}

trait TaskRepository extends Repository[TaskEntity] with CRUDOps[TaskEntity] {
  def list(): Future[Seq[TaskEntity]] = Future {
    store.values.toSeq.sortWith(_.id < _.id)
  }
}

object TaskRepository {
  def apply()(implicit executionContext: ExecutionContext): TaskRepository = new TaskRepositoryImpl()
}