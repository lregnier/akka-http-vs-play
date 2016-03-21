package com.whiteprompt.api

import akka.http.scaladsl.model.StatusCodes
import com.whiteprompt.persistence.Repository
import com.whiteprompt.domain.Task
import com.whiteprompt.utils.AutoMarshaller
import net.codingwell.scalaguice.InjectorExtensions._

class TasksApiSpec extends ApiSpec with TasksRoutes with AutoMarshaller {
  override val repository: Repository[Task] = injector.instance[Repository[Task]]

  "The service" should {

    "return the list of tasks for GET request to /tasks path" in {
      Get("/tasks") ~> tasksRoutes ~> check {
        val result = responseAs[List[Task]]
        val expectedResult = List(
          Task("Task.scala 1", "One description", 1L),
          Task("Task.scala 2", "Another description", 2L))

        result should have size expectedResult.size
        result should contain theSameElementsAs expectedResult
      }
    }

    "get a task for GET request to /task/{idTask} path" in {
      Get("/tasks/1") ~> tasksRoutes ~> check {
        responseAs[Task] shouldEqual Task("Task.scala 1", "One description", 1)
      }
    }

    "create a task for POST request to /task path" in {
      Post("/tasks", Map("name" -> "new name", "description" -> "desc")) ~> tasksRoutes ~> check {
        val task: Task = repository.store.filter(p => p._2.name == "new name").values.head

        task.name shouldEqual "new name"
        task.description shouldEqual "desc"
        response.status shouldEqual StatusCodes.Created
        entityAs[Task] shouldEqual Task("new name", "desc", 3L)
      }
    }

    "update a task for PUT request to /task/{idTask} path" in {
      Put("/tasks/1", Map("name" -> "mod", "description" -> "mod2")) ~> tasksRoutes ~> check {
        val task: Task = repository.store.filter(p => p._1 == 1L).values.head

        task.name shouldEqual "mod"
        task.description shouldEqual "mod2"
        responseAs[Task] shouldEqual Task("mod", "mod2", 1L)
      }
    }

    "delete a task for DELETE request to /task/{idTask} path" in {
      Delete("/tasks/1") ~> tasksRoutes ~> check {
        val task: Option[Task] = repository.store.filter(p => p._1 == 1L).values.headOption

        responseAs[String] shouldEqual "Task with id=1 was deleted"
        task shouldEqual None
      }
    }
  }
}

