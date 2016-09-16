package com.whiteprompt.persistence

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

trait Entity {
  val id: Long
}

trait Repository[T <: Entity] {
  val store: mutable.Map[Long, T]
}

trait CRUDOps[T <: Entity] {
    self: Repository[T] =>

  implicit val ec: ExecutionContext

  def create(e: T): Future[T] = Future {
    store += (e.id -> e)
    e
  }

  def retrieve(id: Long): Future[Option[T]] = Future {
    store.get(id)
  }

  def update(e: T): Future[Option[T]] = Future {
    if(store.isDefinedAt(e.id)) {
      store += (e.id -> e)
      Some(e)
    } else None
  }

  def delete(id: Long): Future[Option[T]] = Future {
    store.remove(id)
  }
}

