package com.github.frossi85.database

import com.github.frossi85.database.tables.{TaskTable, UserTable}
import com.github.frossi85.domain.{Task, User}
import slick.driver.H2Driver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object DB {
  val db = Database.forConfig("h2mem1")
  val users = TableQuery[UserTable]
  val tasks = TableQuery[TaskTable]

  def createSchemas() = {
    db.run(DBIO.seq(
      tablesNotCreated(
        users,
        tasks
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

  private def tablesNotCreated(tables: TableQuery[_ <: Table[_]]*) = {
    tables.filter(table => Await.result(
      db.run(MTable.getTables(table.baseTableRow.tableName)).flatMap { result => Future(result.isEmpty) },
      Duration.Inf
    )).map(table => table.schema).reduceLeft((a, b) => a ++ b)
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
