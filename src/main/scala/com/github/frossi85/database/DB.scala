package com.github.frossi85.database

import slick.lifted.TableQuery
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object DB {
  val db = Database.forConfig("h2mem1")
  val users = TableQuery[UserTable]
  val tasks = TableQuery[TaskTable]

  def createSchemas() = {
    Await.result(db.run(DBIO.seq(
      (
        users.schema ++
        tasks.schema
      ).create
    )), Duration.Inf)
  }

  def dropSchemas() = {
    Await.result(db.run(DBIO.seq((
      users.schema ++
        tasks.schema
      ).drop
    )), Duration.Inf)
  }

  def populateWithDummyData() = {
    val setup = DBIO.seq(
      users += User("frossi85@gmail.com", "11111111"),

      tasks ++= Seq(
        Task("Task.scala 1", "One description", 1L),
        Task("Task.scala 2", "Another description", 1L)
      )
    )
    Await.result(db.run(setup), Duration.Inf)
  }
}
