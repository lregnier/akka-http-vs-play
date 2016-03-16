package com.whiteprompt.database

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

trait Repository[T <: Entity] {
  val store: mutable.HashMap[Long, T]
}

trait CRUDOps[T <: Entity] {
  self: Repository[T] =>

  def cloneWithId(toClone: T, id: Long): T

  def insert(e: T): Future[T] = Future {
    val id: Long = (store.size + 1)
    val withId = cloneWithId(e, id)
    store += (id -> withId)
    withId
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
