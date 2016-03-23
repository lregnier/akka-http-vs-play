package com.whiteprompt.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.pattern.ask
import com.whiteprompt.domain.{Task, TaskRequest}
import com.whiteprompt.services.TaskServiceActor
import com.whiteprompt.services.TaskServiceActor.CreateTask
import com.whiteprompt.utils.AutoMarshaller
import kamon.Kamon
import org.scalatest.{Matchers, WordSpec}
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class TasksRoutesSpec extends WordSpec with Matchers with ScalatestRouteTest {

  Kamon.start()

  trait Context extends TasksRoutes with AutoMarshaller {
    val taskService = system.actorOf(TaskServiceActor.props())
    val task1 = Await.result((taskService ? CreateTask(TaskRequest("Task.scala 1", "One description"))).mapTo[Task], Duration.Inf)
    val task2 = Await.result((taskService ? CreateTask(TaskRequest("Task.scala 2", "Another description"))).mapTo[Task], Duration.Inf)
  }

  "The service" should {
    "create a task for POST request to /task path" in new Context {
      val name = "new name"
      val description = "desc"

      Post("/tasks", TaskRequest(name, description)) ~> tasksRoutes ~> check {
        response.status shouldEqual StatusCodes.Created
        header[Location] shouldBe defined
      }
    }

    "get a task for GET request to /task/{idTask} path" in new Context {
      Get(s"/tasks/${task1.id}") ~> tasksRoutes ~> check {
        responseAs[Task] shouldEqual task1
      }
    }

    "update a task for PUT request to /task/{idTask} path" in new Context {
      val updatedName = "fooName"
      val updatedDescription = "fooDescription"

      Put(s"/tasks/${task1.id}", TaskRequest(updatedName, updatedDescription)) ~> tasksRoutes ~> check {
        responseAs[Task] shouldEqual Task(task1.id, updatedName, updatedDescription)
      }
    }

    "delete a task for DELETE request to /task/{idTask} path" in new Context {
      Delete(s"/tasks/${task1.id}") ~> tasksRoutes ~> check {
        response.status shouldEqual StatusCodes.NoContent
      }
    }

    "return the list of tasks for GET request to /tasks path" in new Context {
      Get("/tasks") ~> tasksRoutes ~> check {
        val result = responseAs[List[Task]]
        val expectedResult = List(task1, task2)

        result should have size expectedResult.size
        result should contain theSameElementsAs expectedResult
      }
    }
  }
}
