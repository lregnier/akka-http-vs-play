import com.whiteprompt.domain.TaskEntity
import org.scalatestplus.play._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.test.Helpers._
import play.api.test._

class ApplicationSpec extends PlaySpec with OneAppPerTest {

  implicit val taskImplicitWrites = Json.writes[TaskEntity]

  trait Context {
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

    val task1 = createTask("TaskRequest.scala 1", "One description")
    val task2 = createTask("TaskRequest.scala 2", "Another description")
  }

  "Play Task Api" must {

    "create a task from a valid POST request to /task path" in new Context {
      val jsonRequest = Json.obj(
        "name" -> "new name",
        "description" -> "desc"
      )
      val task = route(FakeRequest(Helpers.POST, "/v1/tasks", FakeHeaders(Seq(("Content-Type", "application/json"))), jsonRequest.toString)).get

      status(task) mustBe CREATED
      header(LOCATION, task) mustBe defined
    }

    "not create a task from an invalid POST request to /task path" in new Context {
      val jsonRequest = Json.obj(
        "name" -> "",
        "description" -> "desc"
      )
      val task = route(FakeRequest(Helpers.POST, "/v1/tasks", FakeHeaders(Seq(("Content-Type", "application/json"))), jsonRequest.toString)).get

      status(task) mustBe CREATED
    }

    "get a task for GET request to /task/{idTask} path" in new Context {
      val task = route(FakeRequest(GET, s"/v1/tasks/${task1.id}")).get

      status(task) mustBe OK
      Json.parse(contentAsString(task)) mustBe Json.toJson(task1)
    }

    "update a task for PUT request to /task/{idTask} path" in new Context {
      val jsonRequest = Json.obj(
        "name" -> "mod",
        "description" -> "mod2"
      )
      val task = route(FakeRequest(Helpers.PUT, s"/v1/tasks/${task1.id}", FakeHeaders(Seq(("Content-Type", "application/json"))), jsonRequest.toString)).get
      val result = Json.parse(contentAsString(task))

      status(task) mustBe OK
      (result \ "id").get == task1.id
      (result \ "name").get mustBe (jsonRequest \ "name").get
      (result \ "description").get mustBe (jsonRequest \ "description").get
    }

    "delete a task for DELETE request to /task/{idTask} path" in new Context {
      val task = route(FakeRequest(Helpers.DELETE, s"/v1/tasks/${task1.id}")).get
      status(task) mustBe NO_CONTENT
    }

    "return the list of tasks for GET request to /tasks path" in new Context {
      val tasks = route(FakeRequest(GET, "/v1/tasks")).get

      val expectedJson = Json.arr(
        Json.obj(
          "name" -> "TaskRequest.scala 1",
          "description" -> "One description",
          "id" -> 1
        ),
        Json.obj(
          "name" -> "TaskRequest.scala 2",
          "description" -> "Another description",
          "id" -> 2
        )
      )

      status(tasks) mustBe OK
      Json.parse(contentAsString(tasks)) mustBe Json.arr(Json.toJson(task1), Json.toJson(task2))
    }
  }
}