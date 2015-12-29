package com.github.frossi85.services

import com.github.frossi85.database.BaseDao
import com.github.frossi85.database.tables.TaskTable
import com.github.frossi85.domain.Task
import slick.lifted.TableQuery

class TaskService extends BaseDao[TaskTable, Task] {
  override val repository = TableQuery[TaskTable]

  override def copyWithId(entity: Task, id: Long) = entity.copy(id = id)
}
