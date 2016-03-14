package com.github.frossi85.database

import com.github.frossi85.domain.Task

trait TestDB {
  def repository: Repository[Task]

  def initializeRepository() = {
    repository.store.put(1, Task("Task.scala 1", "One description", 1))
    repository.store.put(2, Task("Task.scala 2", "Another description", 2))
  }

  def cleanUpRepository() = {
    repository.store.clear()
  }
}


