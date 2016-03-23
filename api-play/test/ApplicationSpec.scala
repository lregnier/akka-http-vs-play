import org.scalatestplus.play._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.test.Helpers._
import play.api.test._

class ApplicationSpec extends PlaySpec with OneAppPerTest {

  trait Context {
    private def createTask(task: JsObject): JsValue = {
      val result = route(
        FakeRequest(
          Helpers.POST, "/v1/tasks",
          FakeHeaders(Seq(("Content-Type", "application/json"))),
          task.toString
        )
      ).get

      Json.parse(contentAsString(result))
    }

    val task1 = createTask(Json.obj(
      "name" -> "Task.scala 1",
      "description" -> "One description"
    ))

    val task2 = createTask(Json.obj(
      "name" -> "Task.scala 2",
      "description" -> "Another description"
    ))
  }

  "Play Task Api" must {

    "create a task for POST request to /task path" in new Context {
      val jsonRequest = Json.obj(
        "name" -> "new name",
        "description" -> "desc"
      )
      val task = route(FakeRequest(Helpers.POST, "/v1/tasks", FakeHeaders(Seq(("Content-Type", "application/json"))), jsonRequest.toString)).get

      val result = Json.parse(contentAsString(task))

      status(task) mustBe CREATED
      (result \ "name").get mustBe (jsonRequest \ "name").get
      (result \ "description").get mustBe (jsonRequest \ "description").get
    }

    "get a task for GET request to /task/{idTask} path" in new Context {
      val taskId = (task1 \ "id").get
      val task = route(FakeRequest(GET, s"/v1/tasks/$taskId")).get

      status(task) mustBe OK
      Json.parse(contentAsString(task)) mustBe task1
    }

    "update a task for PUT request to /task/{idTask} path" in new Context {
      val taskId = (task1 \ "id").get
      val jsonRequest = Json.obj(
        "name" -> "mod",
        "description" -> "mod2"
      )
      val task = route(FakeRequest(Helpers.PUT, s"/v1/tasks/$taskId", FakeHeaders(Seq(("Content-Type", "application/json"))), jsonRequest.toString)).get
      val result = Json.parse(contentAsString(task))

      status(task) mustBe OK
      (result \ "id").get mustBe taskId
      (result \ "name").get mustBe (jsonRequest \ "name").get
      (result \ "description").get mustBe (jsonRequest \ "description").get
    }

    "delete a task for DELETE request to /task/{idTask} path" in new Context {
      val taskId = (task1 \ "id").get
      val task = route(FakeRequest(Helpers.DELETE, s"/v1/tasks/$taskId")).get

      status(task) mustBe NO_CONTENT
    }

    "return the list of tasks for GET request to /tasks path" in new Context {
      val tasks = route(FakeRequest(GET, "/v1/tasks")).get

      val expectedJson = Json.arr(
        Json.obj(
          "name" -> "Task.scala 1",
          "description" -> "One description",
          "id" -> 1
        ),
        Json.obj(
          "name" -> "Task.scala 2",
          "description" -> "Another description",
          "id" -> 2
        )
      )

      status(tasks) mustBe OK
      Json.parse(contentAsString(tasks)) mustBe Json.arr(task1, task2)
    }
  }
}