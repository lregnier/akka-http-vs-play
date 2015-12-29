package com.github.frossi85.database.tables

import com.github.frossi85.domain.Task
import slick.driver.H2Driver.api._

class TaskTable(tag: Tag) extends EntityWithID[Task](tag, "Tasks") {
  def name = column[String]("name")
  def description = column[String]("description")
  def userId = column[Long]("user_id")

  def * = (name, description, userId, id) <> (Task.tupled, Task.unapply)

  // A reified foreign key relation that can be navigated to create a join
  def user = foreignKey("USER_FK", userId, TableQuery[UserTable])(_.id)
}
