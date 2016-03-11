package com.github.frossi85.api

import akka.http.scaladsl.model.StatusCodes
import com.github.frossi85.database.DB
import com.github.frossi85.domain.Task
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import com.github.frossi85.database.tables.AgnosticDriver.api._
import slick.jdbc.JdbcBackend

class TasksApiSpec extends ApiSpec with TasksApi with AutoMarshaller {

  "The service" should {

    "return the list of tasks for GET request to /tasks path" in {
      Get("/tasks") ~> tasksRoutes ~> check {
        responseAs[List[Task]] shouldEqual List(
          Task("Task.scala 1", "One description", 1L),
          Task("Task.scala 2", "Another description", 2L))
      }
    }

    "get a task for GET request to /task/{idTask} path" in {
      Get("/tasks/1") ~> tasksRoutes ~> check {
        responseAs[Task] shouldEqual Task("Task.scala 1", "One description", 1)
      }
    }

    "create a task for POST request to /task path" in {
      Post("/tasks", Map("name" -> "new name", "description" -> "desc")) ~> tasksRoutes ~> check {
        val task: Task = Await.result(db.run(DB.tasks.filter(_.name === "new name").result.headOption), Duration.Inf).get

        task.name shouldEqual "new name"
        task.description shouldEqual "desc"
        response.status shouldEqual StatusCodes.Created
        entityAs[Task] shouldEqual Task("new name", "desc", 3L)
      }
    }

    "update a task for PUT request to /task/{idTask} path" in {
      Put("/tasks/1", Map("name" -> "mod", "description" -> "mod2")) ~> tasksRoutes ~> check {
        val task: Task = Await.result(db.run(DB.tasks.filter(_.id === 1L).result.headOption), Duration.Inf).get

        task.name shouldEqual "mod"
        task.description shouldEqual "mod2"
        responseAs[Task] shouldEqual Task("mod", "mod2", 1L)
      }
    }

    "delete a task for DELETE request to /task/{idTask} path" in {
      Delete("/tasks/1") ~> tasksRoutes ~> check {
        val task = Await.result(db.run(DB.tasks.filter(_.id === 1L).result.headOption), Duration.Inf)

        responseAs[String] shouldEqual "Task with id=1 was deleted"
        task shouldEqual None
      }
    }
  }
}

