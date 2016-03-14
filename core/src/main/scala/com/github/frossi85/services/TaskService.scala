package com.github.frossi85.services

import javax.inject._
import com.github.frossi85.database.{CRUDOps, Repository}
import com.github.frossi85.domain.Task
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

@Singleton
class TaskService @Inject() () extends Repository[Task] with CRUDOps[Task] with TaskServiceInterface {
  val store = mutable.HashMap[Long, Task]()

  def all: Future[Seq[Task]] = Future(store.values.toSeq)

  override def cloneWithId(toClone: Task, id: Long): Task = toClone.copy(id = id)
}

trait TaskServiceInterface {
  def all: Future[Seq[Task]]

  def byId(id: Long): Future[Option[Task]]

  def insert(item: Task): Future[Task]

  def update(item: Task): Future[Task]

  def delete(id: Long): Future[Long]
}


