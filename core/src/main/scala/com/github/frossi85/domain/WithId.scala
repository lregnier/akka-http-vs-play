package com.github.frossi85.domain

import com.github.frossi85.database.Entity

abstract class WithId extends Cloneable with Entity {
  val id: Long

  override def clone() = super.clone().asInstanceOf[WithId]
}
