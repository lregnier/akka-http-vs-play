package com.github.frossi85.services

// Use H2Driver to connect to an H2 database
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import com.github.frossi85.database.BaseDao
import com.github.frossi85.database.tables.{EntityWithID, UserTable, TaskTable}
import com.github.frossi85.domain.{User, Task}
import slick.lifted.TableQuery

class TaskService extends BaseDao[TaskTable, Task] {
  override val repository = TableQuery[TaskTable]

  override def copyWithId(entity: Task, id: Long) = entity.copy(id = id)
}
