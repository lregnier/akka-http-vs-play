package com.github.frossi85.database.tables

// Use H2Driver to connect to an H2 database
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import com.github.frossi85.domain.Task

class TaskTable(tag: Tag) extends EntityWithID[Task](tag, "Tasks") {
  def name = column[String]("name")
  def description = column[String]("description")
  def userId = column[Long]("user_id")

  def * = (name, description, userId, id) <> (Task.tupled, Task.unapply)

  // A reified foreign key relation that can be navigated to create a join
  def user = foreignKey("USER_FK", userId, TableQuery[UserTable])(_.id)
}
