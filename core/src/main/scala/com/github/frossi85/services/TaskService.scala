package com.github.frossi85.services

import com.github.frossi85.database.{Repository, CRUDOps, BaseDao}
import com.github.frossi85.database.tables.{EntityWithID, TaskTable}
import com.github.frossi85.domain.{WithId, Task}
import com.github.frossi85.database.tables.AgnosticDriver.api._
import slick.jdbc.JdbcBackend
import slick.lifted.TableQuery
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class TaskService(implicit val db: JdbcBackend#Database) extends BaseDao[TaskTable, Task] with TaskServiceInterface {
  override val repository = TableQuery[TaskTable]

  override def copyWithId(entity: Task, id: Long) = entity.copy(id = id)

  def all: Future[Seq[Task]] = {
    val query = repository.result
    db.run(query)
  }
}

class TaskServiceInHashMap(implicit val executionContext: ExecutionContext) extends Repository[Task] with CRUDOps[Task] with TaskServiceInterface {
  val store = mutable.HashMap[Long, Task]()

  def all: Future[Seq[Task]] = Future(store.values.toSeq)
}

trait TaskServiceInterface extends BaseDaoInterface[TaskTable, Task] {
  def all: Future[Seq[Task]]
}

trait BaseDaoInterface[T <: EntityWithID[A], A <: WithId] {
  def byId(id: Long): Future[Option[A]]

  def insert(item: A): Future[A]

  def update(item: A): Future[A]

  def delete(id: Long): Future[Long]
}

