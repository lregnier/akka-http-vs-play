package com.whiteprompt.persistence

import java.util.UUID

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

trait Entity[K] {
  val id: K
}

trait UUIDEntity extends Entity[UUID] {
  val id: UUID
}

trait Repository[K, T <: Entity[K]] {
  val store: mutable.Map[K, T]
}

trait CRUDOps[K, T <: Entity[K]] {
    self: Repository[K, T] =>

  implicit val ec: ExecutionContext

  def create(e: T): Future[T] = Future {
    store += (e.id -> e)
    e
  }

  def retrieve(id: K): Future[Option[T]] = Future {
    store.get(id)
  }

  def update(e: T): Future[Option[T]] = Future {
    if(store.isDefinedAt(e.id)) {
      store += (e.id -> e)
      Some(e)
    } else None
  }

  def delete(id: K): Future[Option[T]] = Future {
    store.remove(id)
  }
}

