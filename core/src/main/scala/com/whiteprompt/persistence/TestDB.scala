package com.whiteprompt.persistence

import com.whiteprompt.domain.Task

trait TestDB {
  def repository: Repository[Task]

  def initializeRepository() = {
    repository.store.put(1, Task(1, "Task.scala 1", "One description"))
    repository.store.put(2, Task(2, "Task.scala 2", "Another description"))
  }

  def cleanUpRepository() = {
    repository.store.clear()
  }
}


