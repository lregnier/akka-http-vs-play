package com.github.frossi85.domain

import com.github.frossi85.database.Entity

case class Task(name: String, description: String, id: Long = 0) extends Entity
