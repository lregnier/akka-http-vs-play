package com.github.frossi85.database.tables

import com.github.frossi85.database.tables.AgnosticDriver.api._
import com.github.frossi85.domain.Task

class TaskTable(tag: Tag) extends EntityWithID[Task](tag, "Tasks") {
  def name = column[String]("name")
  def description = column[String]("description")

  def * = (name, description, id) <>(Task.tupled, Task.unapply)
}
