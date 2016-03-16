
/*

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification with TestDB {
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

  "Play Task Api" should {
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

        status(tasks) must equalTo(OK)
        Json.parse(contentAsString(tasks)) must be equalTo(expectedJson)
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

        status(task) must equalTo(OK)
        Json.parse(contentAsString(task)) must be equalTo(expectedJson)
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

        status(task) must equalTo(CREATED)
        Json.parse(contentAsString(task)) must be equalTo(expectedJson)
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

        status(task) must equalTo(OK)
        Json.parse(contentAsString(task)) must be equalTo(expectedJson)
      }
    }

    "delete a task for DELETE request to /task/{idTask} path" in {
      runningWithRepository(app) {
        val task = route(FakeRequest(Helpers.DELETE, "/v1/tasks/1")).get

        status(task) must equalTo(OK)
        contentAsString(task) must be equalTo("Task with id=1 was deleted")
      }
    }
  }
}
*/