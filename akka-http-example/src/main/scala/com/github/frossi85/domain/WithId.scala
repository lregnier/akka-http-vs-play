package com.github.frossi85.domain

abstract class WithId extends Cloneable {
  def id: Long

  override def clone() = super.clone().asInstanceOf[WithId]
}
