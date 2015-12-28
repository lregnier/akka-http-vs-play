package com.github.frossi85.database

// Use H2Driver to connect to an H2 database
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import com.github.frossi85.database.tables.{EntityWithID, UserTable, TaskTable}
import com.github.frossi85.domain.{WithId, User, Task}
import slick.lifted.TableQuery

import scala.concurrent.Future

trait DB[T <: EntityWithID[A], A <: WithId] {
  val db = Database.forConfig("h2mem1")
  val users = TableQuery[UserTable]
  val tasks = TableQuery[TaskTable]

  def setUp() = {
      db.run(DBIO.seq((
              users.schema ++
              tasks.schema
          ).create
      ))
  }

  val repository: TableQuery[T]

  def copyWithId(entity: A, id: Long): A

  def populateWithDummyData = {
    val setup = DBIO.seq(
      // Insert some suppliers
      users += User("frossi85@gmail.com", "11111111"),

      // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
      tasks ++= Seq(
        Task("Task.scala 1", "One description", 1L),
        Task("Task.scala 2", "Another description", 1L)
      )
    )
    db.run(setup)
  }


  def byId(id: Long): Future[Option[A]] =  {
    val query = repository.filter(_.id === id)
    db.run(query.result.headOption)
  }

  def insert(item: A): Future[A] = {
    val result = db.run((repository returning repository.map(_.id)) += item)

    result.map(x => copyWithId(item, x))
  }

  def update(item: A): Future[A] = {
    val returnEvent = for {
      rowsAffected <- repository.filter(_.id === item.id).update(item)
      returningEvent <- rowsAffected match {
        case 1 => DBIO.successful(item)
        case n => DBIO.failed(new Exception("No event updated"))
      }
    } yield returningEvent

    db.run(returnEvent)
  }

  def delete(id: Long): Future[Long] = {
    val returningId = for {
      rowsAffected <- repository.filter(_.id === id).delete
      result <- rowsAffected match {
        case 1 => DBIO.successful(id)
        case n => DBIO.failed(new Exception("No event deleted"))
      }
    } yield result

    db.run(returningId)
  }
}