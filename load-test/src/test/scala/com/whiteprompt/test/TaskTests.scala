import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

object TaskTests {
  private def randomString = Random.alphanumeric.take(10).mkString
  private def taskData =  s"""{
    "name": "$randomString",
    "description": "$randomString"
  }"""

  val createTask = exec(
    http("CreateTask")
      .post("/tasks")
      .header("Content-Type", "application/json")
      .body(StringBody(taskData))
      .asJSON
      .check(jsonPath("$.id").saveAs("taskId"))
      .check(status is 201)
  )

  val retrieveTask = exec(
    http("RetrieveTask")
      .get("/tasks/${taskId}")
      .header("Content-Type", "application/json")
      .asJSON
      .check(jsonPath("$.id"))
      .check(status is 200)
  )

  val updateTask = exec(
    http("UpdateTask")
      .put("/tasks/${taskId}")
      .header("Content-Type", "application/json")
      .body(StringBody(taskData))
      .asJSON
      .check(jsonPath("$.id"))
      .check(status is 200)
  )

  val deleteTask = exec(
    http("DeleteTask")
      .delete("/tasks/${taskId}")
      .header("Content-Type", "application/json")
      .asJSON
      .check(status is 200)
  )

  val listTasks = exec(
    http("ListTasks")
      .get("/tasks")
      .header("Content-Type", "application/json")
      .asJSON
      .check(status is 200)
  )
}

class TaskTests extends Simulation {
  import TaskTests._

  val host = "localhost"
  val port = 9000

  val scenarioName = "CreateUpdateListViewTasks"

  val httpProtocol = http
    .baseURL(s"http://$host:$port/v1")

  val scn = scenario(scenarioName)
    .exec(
      createTask,
      retrieveTask,
      updateTask,
      listTasks,
      deleteTask
    )

  setUp(scn.inject(rampUsers(10) over (10 seconds))).protocols(httpProtocol)
}