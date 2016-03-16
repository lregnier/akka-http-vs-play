import com.github.frossi85.database.{Repository, TestDB}
import com.github.frossi85.domain.Task
import com.github.frossi85.services.{TaskService, TaskServiceInterface}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._
import org.scalatestplus.play._

class ApplicationSpec extends PlaySpec with TestDB  {
  val service = new TaskService

  def app = new GuiceApplicationBuilder()
    .overrides(bind[TaskServiceInterface].to(service))
    .build

  def repository: Repository[Task]  = service

  def runningWithRepository[T](app : play.api.Application)(block : => T) : T = {
    running(app)({
      initializeRepository()
      val result = block
      cleanUpRepository()
      result
    })
  }

  "Play Task Api" must {
    "return the list of tasks for GET request to /tasks path" in {
      runningWithRepository(app) {
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
        Json.parse(contentAsString(tasks)) mustBe expectedJson
      }
    }

    "get a task for GET request to /task/{idTask} path" in {
      runningWithRepository(app) {
        val task = route(FakeRequest(GET, "/v1/tasks/1")).get

        val expectedJson = Json.obj(
          "name" -> "Task.scala 1",
          "description" -> "One description",
          "id" -> 1
        )

        status(task) mustBe OK
        Json.parse(contentAsString(task)) mustBe expectedJson
      }
    }

    "create a task for POST request to /task path" in {
      runningWithRepository(app) {
        val jsonRequest = Json.obj(
          "name" -> "new name",
          "description" -> "desc"
        )

        val task = route(FakeRequest(Helpers.POST, "/v1/tasks", FakeHeaders(Seq(("Content-Type", "application/json"))), jsonRequest.toString)).get

        val expectedJson = Json.obj(
          "name" -> "new name",
          "description" -> "desc",
          "id" -> 3
        )

        status(task) mustBe CREATED
        Json.parse(contentAsString(task)) mustBe expectedJson
      }
    }


    "update a task for PUT request to /task/{idTask} path" in {
      runningWithRepository(app) {
        val jsonRequest = Json.obj(
          "name" -> "mod",
          "description" -> "mod2"
        )
        val task = route(FakeRequest(Helpers.PUT, "/v1/tasks/1", FakeHeaders(Seq(("Content-Type", "application/json"))), jsonRequest.toString)).get

        val expectedJson = Json.obj(
          "name" -> "mod",
          "description" -> "mod2",
          "id" -> 1
        )

        status(task) mustBe OK
        Json.parse(contentAsString(task)) mustBe expectedJson
      }
    }

    "delete a task for DELETE request to /task/{idTask} path" in {
      runningWithRepository(app) {
        val task = route(FakeRequest(Helpers.DELETE, "/v1/tasks/1")).get

        status(task) mustBe OK
        contentAsString(task) mustBe "Task with id=1 was deleted"
      }
    }
  }
}
