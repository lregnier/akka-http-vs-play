import com.github.frossi85.domain.Task
import modules.TestDatabaseProvider
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {
  import play.api.inject.bind
  import play.api.inject.guice.GuiceApplicationBuilder

  def app = new GuiceApplicationBuilder()
    .overrides(bind(classOf[slick.jdbc.JdbcBackend.Database]).toProvider(classOf[TestDatabaseProvider]))
    .build

  "Play Task Api" should {
    "return the list of tasks for GET request to /tasks path" in {
      running(app) {
        val tasks = route(FakeRequest(GET, "/v1/tasks")).get

        val expectedJson = Json.arr(
          Json.obj(
            "name" -> "Task.scala 1",
            "description" -> "One description",
            "userId" -> 1,
            "id" -> 1
          ),
          Json.obj(
            "name" -> "Task.scala 2",
            "description" -> "Another description",
            "userId" -> 1,
            "id" -> 2
          )
        )

        status(tasks) must equalTo(OK)
        Json.parse(contentAsString(tasks)) must be equalTo(expectedJson)
      }
    }

    "get a task for GET request to /task/{idTask} path" in {
      running(app) {
        val task = route(FakeRequest(GET, "/v1/tasks/1")).get

        val expectedJson = Json.obj(
          "name" -> "Task.scala 1",
          "description" -> "One description",
          "userId" -> 1,
          "id" -> 1
        )

        status(task) must equalTo(OK)
        Json.parse(contentAsString(task)) must be equalTo(expectedJson)
      }
    }

    "create a task for POST request to /task path" in {
      running(app) {
        val jsonRequest = Json.obj(
          "name" -> "new name",
          "description" -> "desc"
        )

        val task = route(FakeRequest(Helpers.POST, "/v1/tasks", FakeHeaders(Seq(("Content-Type", "application/json"))), jsonRequest.toString)).get

        val expectedJson = Json.obj(
          "name" -> "new name",
          "description" -> "desc",
          "userId" -> 1,
          "id" -> 3
        )

        status(task) must equalTo(CREATED)
        Json.parse(contentAsString(task)) must be equalTo(expectedJson)
      }
    }


    "update a task for PUT request to /task/{idTask} path" in {
      running(app) {
        val jsonRequest = Json.obj(
          "name" -> "mod",
          "description" -> "mod2"
        )
        val task = route(FakeRequest(Helpers.PUT, "/v1/tasks/1", FakeHeaders(Seq(("Content-Type", "application/json"))), jsonRequest.toString)).get

        val expectedJson = Json.obj(
          "name" -> "mod",
          "description" -> "mod2",
          "userId" -> 1,
          "id" -> 1
        )

        status(task) must equalTo(OK)
        Json.parse(contentAsString(task)) must be equalTo(expectedJson)
      }
    }

    "delete a task for DELETE request to /task/{idTask} path" in {
      running(app) {
        val task = route(FakeRequest(Helpers.DELETE, "/v1/tasks/1")).get

        status(task) must equalTo(OK)
        contentAsString(task) must be equalTo("Task with id=1 was deleted")
      }
    }
  }
}
