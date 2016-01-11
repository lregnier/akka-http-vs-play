package com.github.frossi85

import com.github.frossi85.api.{AutoMarshaller, TasksApi}
import com.github.frossi85.database.DB
import com.github.frossi85.domain.Task
import slick.driver.H2Driver.api._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class HealthCheckSpec extends ApiSpec with Routes {

  "The service" should {

    "return a greeting for GET requests to the root path 222222" in {
      // tests:
      Get("/health-check") ~> routes ~> check {
        responseAs[String] shouldEqual "It's Alive"
      }
    }
  }
}


class TasksApiSpec extends ApiSpec with TasksApi with AutoMarshaller {

  "The service" should {

    "return the list of task for GET request to /task path" in {
      Get("/tasks") ~> tasksRoutes ~> check {
        responseAs[List[Task]] shouldEqual List(
          Task("Task.scala 1", "One description", 1, 1),
          Task("Task.scala 2", "Another description", 1, 2))
      }
    }

    "get a task for GET request to /task/{idTask} path" in {
      Get("/tasks/1") ~> tasksRoutes ~> check {
        responseAs[Task] shouldEqual Task("Task.scala 1", "One description", 1, 1)
      }
    }

    "create a task for POST request to /task path" in {
      Post("/tasks", Map("name" -> "name", "description" -> "desc")) ~> tasksRoutes ~> check {
        val task: Task = Await.result(db.run(DB.tasks.filter(_.name === "name").result.headOption), Duration.Inf).get

        task.name shouldEqual "name"
        task.description shouldEqual "desc"
        responseAs[Task] shouldEqual Task("name", "desc", 1, 3)
      }
    }

    "update a task for PUT request to /task/{idTask} path" in {
      Put("/tasks/1", Map("name" -> "mod", "description" -> "mod2")) ~> tasksRoutes ~> check {
        val task: Task = Await.result(db.run(DB.tasks.filter(_.id === 1L).result.headOption), Duration.Inf).get

        task.name shouldEqual "mod"
        task.description shouldEqual "mod2"
        responseAs[Task] shouldEqual Task("mod", "mod2", 1, 1)
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
