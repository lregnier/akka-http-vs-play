package com.github.frossi85.domain

case class Task(name: String, description: String, id: Long = 0) extends WithId
