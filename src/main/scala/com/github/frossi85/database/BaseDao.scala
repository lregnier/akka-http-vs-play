package com.github.frossi85.database

import slick.jdbc.JdbcBackend
import slick.lifted.TableQuery
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BaseDao[T <: EntityWithID[A], A <: WithId] {
  val db: JdbcBackend#Database

  val repository: TableQuery[T]

  def copyWithId(entity: A, id: Long): A

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