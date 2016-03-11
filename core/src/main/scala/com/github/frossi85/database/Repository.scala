package com.github.frossi85.database


import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

trait Repository[T <: Entity] {
  implicit val executionContext: ExecutionContext

  val store: mutable.HashMap[Long, T]
}

trait CRUDOps[T <: Entity] {
  self: Repository[T] =>

  def insert(e: T): Future[T] = Future {
    store += (e.id -> e)
    e
  }

  def byId(id: Long): Future[Option[T]] = Future {
    store.get(id)
  }

  def update(e: T): Future[T] = Future {
    store += (e.id -> e)
    e
  }

  def delete(id: Long): Future[Long] = Future {
    store.remove(id)
    id
  }
}

trait Entity {
  val id: Long
}
