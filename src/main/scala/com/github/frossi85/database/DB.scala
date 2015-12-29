package com.github.frossi85.database

import com.github.frossi85.database.tables.{TaskTable, UserTable}
import com.github.frossi85.domain.{Task, User}
import slick.driver.H2Driver.api._
import slick.lifted.TableQuery

object DB {
  val db = Database.forConfig("h2mem1")
  val users = TableQuery[UserTable]
  val tasks = TableQuery[TaskTable]

  def createSchemas() = {
    db.run(DBIO.seq((
      users.schema ++
        tasks.schema
      ).create
    ))
  }

  def dropSchemas() = {
    db.run(DBIO.seq((
      users.schema ++
        tasks.schema
      ).drop
    ))
  }

  def populateWithDummyData() = {
    val setup = DBIO.seq(
      users += User("frossi85@gmail.com", "11111111"),

      tasks ++= Seq(
        Task("Task.scala 1", "One description", 1L),
        Task("Task.scala 2", "Another description", 1L)
      )
    )
    db.run(setup)
  }
}
