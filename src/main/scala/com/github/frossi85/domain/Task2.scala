package com.github.frossi85.domain

case class Task(name: String, description: String, userId: Long, id: Long = 0) extends WithId
