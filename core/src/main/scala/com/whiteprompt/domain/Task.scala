package com.whiteprompt.domain

import com.whiteprompt.database.Entity

case class Task(name: String, description: String, id: Long = 0) extends Entity
