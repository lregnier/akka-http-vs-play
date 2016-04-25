package com.whiteprompt.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.whiteprompt.TestData
import com.whiteprompt.api.utils.AutoMarshaller
import com.whiteprompt.domain.TaskEntity
import com.whiteprompt.services.TaskServiceActor
import kamon.Kamon
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class TasksRoutesSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterAll {

  override def beforeAll {
    Kamon.start()
  }

  override def afterAll {
    Kamon.shutdown()
  }

  trait Scope extends TaskRoutes with AutoMarshaller with TestData {
    val context = system.dispatcher
    taskRepository.init()
    val taskService = system.actorOf(TaskServiceActor.props(taskRepository))
  }

  "When sending a POST request, the Task API" should {
    "create a new Task and return a 201 Response if the request is valid" in new Scope {
      val name = "Create name"
      val description = "Create description"
      Post("/tasks", TaskRequest(name, description)) ~> tasksRoutes ~> check {
        response.status shouldEqual StatusCodes.Created
        header[Location] shouldBe defined
      }
    }
    "not create a Task and return a 400 Response if the request is not valid" in new Scope {
      val name = "" // Name must not be empty
      val description = "Create description"
      Post("/tasks", Map("name" -> name, "description" -> description)) ~> Route.seal(tasksRoutes) ~> check {
        response.status shouldEqual StatusCodes.BadRequest
      }
    }
  }

  "When sending a GET request, the Task API" should {
    "return a 200 Response with the requested Task if it exists" in new Scope {
      Get(s"/tasks/${taskEntity1.id}") ~> tasksRoutes ~> check {
        responseAs[TaskEntity] shouldEqual taskEntity1
      }
    }
    "return a 404 Response if the requested Task does not exist" in new Scope {
      Get(s"/tasks/$nonExistentTaskId") ~> Route.seal(tasksRoutes) ~> check {
        response.status shouldEqual StatusCodes.NotFound
      }
    }
  }

  "When sending a PUT request, the Task API" should {
    "update the Task with the given data and return it back in a 200 Response" in new Scope {
      val updatedId = taskEntity1.id
      val updatedName = "Updated name"
      val updatedDescription = "Updated description"
      Put(s"/tasks/$updatedId", TaskRequest(updatedName, updatedDescription)) ~> tasksRoutes ~> check {
        responseAs[TaskEntity] shouldEqual TaskEntity(updatedId, updatedName, updatedDescription)
      }
    }
    "return a 404 Response if the requested Task does not exist" in new Scope {
      val updatedId = nonExistentTaskId
      val updatedName = "fooName"
      val updatedDescription = "fooDescription"
      Put(s"/tasks/$updatedId", TaskRequest(updatedName, updatedDescription)) ~> Route.seal(tasksRoutes) ~> check {
        response.status shouldEqual StatusCodes.NotFound
      }
    }
  }

  "When sending a DELETE request, the Task API" should {
    "delete the requested Task and return a 204 Response if the Task exists" in new Scope {
      Delete(s"/tasks/${taskEntity1.id}") ~> tasksRoutes ~> check {
        response.status shouldEqual StatusCodes.NoContent
      }
    }
    "return a 404 Response if the requested Task does not exist" in new Scope {
      val nonExisentTaskId = 1234L
      Delete(s"/tasks/$nonExistentTaskId") ~> Route.seal(tasksRoutes) ~> check {
        response.status shouldEqual StatusCodes.NotFound
      }
    }
  }

  "When sending a GET request, the Task API" should {
    "return a list of all Tasks" in new Scope {
      Get("/tasks") ~> tasksRoutes ~> check {
        val result = responseAs[List[TaskEntity]]
        result should have size taskRepository.size
      }
    }
  }
}
