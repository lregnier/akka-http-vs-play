package com.github.frossi85.services

import com.github.frossi85.database.BaseDao
import com.github.frossi85.database.tables.TaskTable
import com.github.frossi85.domain.Task
import com.github.frossi85.database.tables.AgnosticDriver.api._
import slick.jdbc.JdbcBackend
import slick.lifted.TableQuery
import scala.concurrent.Future

class TaskService(implicit val db: JdbcBackend#Database) extends BaseDao[TaskTable, Task] {
  override val repository = TableQuery[TaskTable]

  override def copyWithId(entity: Task, id: Long) = entity.copy(id = id)

  def byUser(userId: Long): Future[Seq[Task]] = {
    val query = repository.filter(_.userId === userId).result
    db.run(query)
  }
}

