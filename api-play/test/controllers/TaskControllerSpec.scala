package controllers

import com.whiteprompt.domain.TaskEntity
import org.scalatestplus.play._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

class TaskControllerSpec extends PlaySpec with OneAppPerTest {

  implicit val taskImplicitWrites = Json.writes[TaskEntity]

  trait Scope {
    private def createTask(name: String, description: String): TaskEntity = {
      val requestData = Json.obj(
        "name" -> name,
        "description" -> description
      )

      val result = route(
        FakeRequest(
          Helpers.POST, "/v1/tasks",
          FakeHeaders(Seq(("Content-Type", "application/json"))),
          requestData.toString
        )
      ).get

      val taskId = header(LOCATION, result).map(_.split('/').last.toLong).get

      TaskEntity(taskId, name, description)
    }

    val taskEntity1 = createTask("TaskRequest.scala 1", "One description")
    val taskEntity2 = createTask("TaskRequest.scala 2", "Another description")

    val nonExistentTaskId = 1234L
  }

  "When sending a POST request, the Task API" should {
    "create a new Task and return a 201 Response if the request is valid" in new Scope {
      val jsonRequest = Json.obj(
        "name" -> "Create name",
        "description" -> "Create description"
      )
      val response = route(FakeRequest(Helpers.POST, "/v1/tasks", FakeHeaders(Seq(("Content-Type", "application/json"))), jsonRequest.toString)).get
      status(response) mustBe CREATED
      header(LOCATION, response) mustBe defined
    }
    "not create a Task and return a 400 Response if the request is not valid" in new Scope {
      val jsonRequest = Json.obj(
        "name" -> "", // Name must not be empty
        "description" -> "Create description"
      )
      val response = route(FakeRequest(Helpers.POST, "/v1/tasks", FakeHeaders(Seq(("Content-Type", "application/json"))), jsonRequest.toString)).get
      status(response) mustBe BAD_REQUEST
    }
  }

  "When sending a GET request, the Task API" should {
    "return a 200 Response with the requested Task if it exists" in new Scope {
      val response = route(FakeRequest(GET, s"/v1/tasks/${taskEntity1.id}")).get
      status(response) mustBe OK
      Json.parse(contentAsString(response)) mustBe Json.toJson(taskEntity1)

    }
    "return a 404 Response if the requested Task does not exist" in new Scope {
      val response = route(FakeRequest(GET, s"/v1/tasks/$nonExistentTaskId")).get
      status(response) mustBe NOT_FOUND
    }
  }

  "When sending a PUT request, the Task API" should {
    "update the Task with the given data and return it back in a 200 Response" in new Scope {
      val updatedId = taskEntity1.id
      val jsonRequest = Json.obj(
        "name" -> "Updated name",
        "description" -> "Updated description"
      )
      val response = route(FakeRequest(Helpers.PUT, s"/v1/tasks/$updatedId", FakeHeaders(Seq(("Content-Type", "application/json"))), jsonRequest.toString)).get
      val result = Json.parse(contentAsString(response))

      status(response) mustBe OK
      (result \ "id").get == taskEntity1.id
      (result \ "name").get mustBe (jsonRequest \ "name").get
      (result \ "description").get mustBe (jsonRequest \ "description").get
    }
    "not update the Task and return a 400 Response if the request is not valid" in new Scope {
      val jsonRequest = Json.obj(
        "name" -> "", // Name must not be empty
        "description" -> "Create description"
      )
      val response = route(FakeRequest(Helpers.PUT, s"/v1/tasks/${taskEntity1.id}", FakeHeaders(Seq(("Content-Type", "application/json"))), jsonRequest.toString)).get
      status(response) mustBe BAD_REQUEST
    }
    "return a 404 Response if the requested Task does not exist" in new Scope {
      val updatedId = nonExistentTaskId
      val jsonRequest = Json.obj(
        "name" -> "Updated name",
        "description" -> "Updated description"
      )
      val response = route(FakeRequest(Helpers.PUT, s"/v1/tasks/$updatedId", FakeHeaders(Seq(("Content-Type", "application/json"))), jsonRequest.toString)).get
      status(response) mustBe NOT_FOUND
    }
  }

  "When sending a DELETE request, the Task API" should {
    "delete the requested Task and return a 204 Response if the Task exists" in new Scope {
      val response = route(FakeRequest(Helpers.DELETE, s"/v1/tasks/${taskEntity1.id}")).get
      status(response) mustBe NO_CONTENT
    }
    "return a 404 Response if the requested Task does not exist" in new Scope {
      val response = route(FakeRequest(Helpers.DELETE, s"/v1/tasks/$nonExistentTaskId")).get
      status(response) mustBe NOT_FOUND
    }
  }

  "When sending a GET request, the Task API" should {
    "return a list of all Tasks" in new Scope {
      val response = route(FakeRequest(GET, "/v1/tasks")).get
      status(response) mustBe OK
      Json.parse(contentAsString(response)) mustBe Json.arr(Json.toJson(taskEntity1), Json.toJson(taskEntity2))
    }
  }

}