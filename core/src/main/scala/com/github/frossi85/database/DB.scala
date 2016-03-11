package com.github.frossi85.database

import com.github.frossi85.database.tables.TaskTable
import com.github.frossi85.domain.Task
import com.typesafe.config.{ConfigFactory, Config}
import slick.lifted.TableQuery
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import com.github.frossi85.database.tables.AgnosticDriver.api._

object DB {
  val db = Database.forConfig("h2mem1")

  val tasks = TableQuery[TaskTable]

  def createSchemas() = {
    Await.result(db.run(DBIO.seq(
      (
        tasks.schema
      ).create
    )), Duration.Inf)
  }

  def dropSchemas() = {
    Await.result(db.run(DBIO.seq(
      (
        tasks.schema
      ).drop
    )), Duration.Inf)
  }

  def populateWithDummyData() = {
    val setup = DBIO.seq(
      tasks ++= Seq(
        Task("Task.scala 1", "One description", 1L),
        Task("Task.scala 2", "Another description", 1L)
      )
    )
    Await.result(db.run(setup), Duration.Inf)
  }
}
