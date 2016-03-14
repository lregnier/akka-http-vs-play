package com.github.frossi85

import com.github.frossi85.database.Repository
import com.github.frossi85.domain.Task
import com.github.frossi85.services.{TaskService, TaskServiceInterface}
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

class ServicesModule extends AbstractModule with ScalaModule {
  override def configure() {
    bind[Repository[Task]].to[TaskService].asEagerSingleton()
    bind[TaskServiceInterface].to[TaskService].asEagerSingleton()
  }
}