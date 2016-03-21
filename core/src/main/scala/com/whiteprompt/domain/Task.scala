package com.whiteprompt.domain

import com.whiteprompt.persistence.Entity

case class Task(id: Long, name: String, description: String) extends Entity
