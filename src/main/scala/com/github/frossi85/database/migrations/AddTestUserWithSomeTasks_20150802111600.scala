package com.github.frossi85.database.migrations

import com.github.frossi85.domain.{Task, User}
import com.github.frossi85.services.{TaskService, UserService}
import slick.jdbc.JdbcBackend
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class AddTestUserWithSomeTasks_20150802111600(implicit val db: JdbcBackend#Database, session : slick.jdbc.JdbcBackend#SessionDef, dialect : slick.migration.api.Dialect[_]) extends Migration {
  val userService = new UserService
  val taskService = new TaskService

  override def up(): Unit = {
    sideEffects(() => {
      Await.result((for {
        f1 <- userService.insert(User("test@test.com", "test", 1))
        f2 <- taskService.insert(Task("Task.scala 1", "One description", 1L))
        f3 <- taskService.insert(Task("Task.scala 2", "Another description", 1L))
      } yield (f1, f2, f3)), Duration.Inf)
    })
  }

  override def down(): Unit = {
    //Drop things to revert migration goes here
    sideEffects(() => {
      Await.result(userService.byEmail("test@test.com").map(x => userService.delete(x.id)), Duration.Inf)
    })
  }
}




