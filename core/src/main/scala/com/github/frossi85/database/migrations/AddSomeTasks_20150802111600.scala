package com.github.frossi85.database.migrations

import com.github.frossi85.domain.Task
import com.github.frossi85.services.TaskService
import slick.jdbc.JdbcBackend
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class AddSomeTasks_20150802111600(implicit val db: JdbcBackend#Database, session : slick.jdbc.JdbcBackend#SessionDef, dialect : slick.migration.api.Dialect[_]) extends Migration {
  val taskService = new TaskService

  override def up(): Unit = {
    sideEffects(() => {
      Await.result((for {
        f1 <- taskService.insert(Task("Task.scala 1", "One description"))
        f2 <- taskService.insert(Task("Task.scala 2", "Another description"))
      } yield (f1, f2)), Duration.Inf)
    })
  }

  override def down(): Unit = {
    //Drop things to revert migration goes here

  }
}




