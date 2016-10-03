package controllers

import java.io.File

import com.whiteprompt.TestData
import com.whiteprompt.domain.TaskEntity
import com.whiteprompt.services.TaskServiceActor
import org.scalatest.{BeforeAndAfterAll, TestData => ScalaTestData}
import org.scalatestplus.play._
import play.api.ApplicationLoader.Context
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._
import play.api.{Application, ApplicationLoader, _}
import router.Routes

class TaskControllerSpec extends PlaySpec with BeforeAndAfterAll with OneAppPerTest with TestData {

  implicit val taskImplicitReads = Json.writes[TaskData]
  implicit val taskImplicitWrites = Json.reads[TaskEntity]

  class TestApplicationComponents(context: Context) extends BuiltInComponentsFromContext(context) {
    implicit val ec = actorSystem.dispatcher
    val taskService = actorSystem.actorOf(TaskServiceActor.props(taskRepository()), "task-service")

    lazy val healthCheckController = new HealthCheckController()
    lazy val taskController = new TaskController(taskService)
    lazy val assets = new Assets(httpErrorHandler)
    override lazy val router = new Routes(httpErrorHandler, healthCheckController, taskController, assets)

  }

  override def newAppForTest(testData: ScalaTestData): Application = {
    val context = ApplicationLoader.createContext(
      new Environment(new File("."), ApplicationLoader.getClass.getClassLoader, Mode.Test)
    )
    new TestApplicationComponents(context).application
  }

  "When sending a POST request, the Task API" should {
    "create a new Task and return a 201 Response if the request is valid" in {
      val requestBody = Json.obj(
        "name" -> "Create name",
        "description" -> "Create description"
      )
      val response = route(FakeRequest(Helpers.POST, "/v1/tasks", FakeHeaders(Seq(("Content-Type", "application/json"))), requestBody.toString)).get
      status(response) mustBe CREATED
      header(LOCATION, response) mustBe defined
    }
    "not create a Task and return a 400 Response if the request is not valid" in {
      val requestBody = Json.obj(
        "name" -> "", // Name must not be empty
        "description" -> "Create description"
      )

      val response = route(FakeRequest(Helpers.POST, "/v1/tasks", FakeHeaders(Seq(("Content-Type", "application/json"))), requestBody)).get
      status(response) mustBe BAD_REQUEST
    }
  }

  "When sending a GET request, the Task API" should {
    "return a 200 Response with the requested Task if it exists" in {
      val response = route(FakeRequest(GET, s"/v1/tasks/${taskEntity1.id}")).get
      status(response) mustBe OK
      contentAsJson(response).as[TaskEntity] mustBe taskEntity1
    }
    "return a 404 Response if the requested Task does not exist" in {
      val response = route(FakeRequest(GET, s"/v1/tasks/$nonExistentTaskId")).get
      status(response) mustBe NOT_FOUND
    }
  }

  "When sending a PUT request, the Task API" should {
    "update the Task with the given data and return it back in a 200 Response" in {
      val updatedId = taskEntity1.id
      val updatedName = "Updated name"
      val updatedDescription = "Updated description"

      val requestBody = Json.obj(
        "name" -> updatedName,
        "description" -> updatedDescription
      )

      val response = route(FakeRequest(Helpers.PUT, s"/v1/tasks/$updatedId", FakeHeaders(Seq(("Content-Type", "application/json"))), requestBody.toString)).get

      status(response) mustBe OK
      contentAsJson(response).as[TaskEntity] mustEqual TaskEntity(updatedId, updatedName, updatedDescription)
    }
    "not update the Task and return a 400 Response if the request is not valid" in {
      val requestBody = Json.obj(
        "name" -> "", // Name must not be empty
        "description" -> "Create description"
      )
      val response = route(FakeRequest(Helpers.PUT, s"/v1/tasks/${taskEntity1.id}", FakeHeaders(Seq(("Content-Type", "application/json"))), requestBody.toString)).get
      status(response) mustBe BAD_REQUEST
    }
    "return a 404 Response if the requested Task does not exist" in {
      val updatedId = nonExistentTaskId
      val requestBody = Json.obj(
        "name" -> "Updated name",
        "description" -> "Updated description"
      )
      val response = route(FakeRequest(Helpers.PUT, s"/v1/tasks/$updatedId", FakeHeaders(Seq(("Content-Type", "application/json"))), requestBody.toString)).get
      status(response) mustBe NOT_FOUND
    }
  }

  "When sending a DELETE request, the Task API" should {
    "delete the requested Task and return a 204 Response if the Task exists" in {
      val response = route(FakeRequest(Helpers.DELETE, s"/v1/tasks/${taskEntity1.id}")).get
      status(response) mustBe NO_CONTENT
    }
    "return a 404 Response if the requested Task does not exist" in {
      val response = route(FakeRequest(Helpers.DELETE, s"/v1/tasks/$nonExistentTaskId")).get
      status(response) mustBe NOT_FOUND
    }
  }

  "When sending a GET request, the Task API" should {
    "return a list of all Tasks" in {
      val response = route(FakeRequest(GET, "/v1/tasks")).get
      status(response) mustBe OK
      contentAsJson(response).as[List[TaskEntity]] must contain theSameElementsAs(Seq(taskEntity1, taskEntity2))
    }
  }

}