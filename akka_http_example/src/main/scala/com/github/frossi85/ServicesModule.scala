package com.github.frossi85

import akka.actor.Actor
import com.github.frossi85.services.{TaskService, TaskServiceInterface, TaskActor}
import net.codingwell.scalaguice.ScalaModule
import com.google.inject.AbstractModule
import com.google.inject.name.Names

class ServicesModule extends AbstractModule with ScalaModule {
  override def configure() {
    bind[TaskServiceInterface].to[TaskService].asEagerSingleton()
  }
}