import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

/**
 * This object provides the scenario parts of the load tests.
 */
sealed trait TaskLoadTest extends Simulation {
  val host = "localhost"
  val port = 9000
  val apiVersion = "v1"
  val baseURL = s"http://$host:$port/$apiVersion"
  def scenarioName: String

  val httpProtocol = http.baseURL(s"http://$host:$port/$apiVersion")

  private def randomString = Random.alphanumeric.take(10).mkString
  private def taskData =  s"""{
    "name":"tom",
    "description":"tomtask"
  }"""

  val extractTaskIdFromLocation = headerRegex("Location", "http://localhost:9000/v1/tasks/(.*)").ofType[String]

  val createTask = exec(
    http("Create a task")
      .post("/tasks")
      .header("Content-Type", "application/json")
      .body(StringBody(taskData)).asJSON
      .check(extractTaskIdFromLocation.saveAs("taskId"))
      .check(status is 201)
  )

  val retrieveTask = exec(
    http("Retrieve a task")
      .get("/tasks/${taskId}")
      .header("Content-Type", "application/json")
      .asJSON
      .check(status is 200)
  )

  val updateTask = exec(
    http("Update a task")
      .put("/tasks/${taskId}")
      .header("Content-Type", "application/json")
      .body(StringBody(taskData))
      .asJSON
      .check(status is 200)
  )

  val deleteTask = exec(
    http("Delete a task")
      .delete("/tasks/${taskId}")
      .header("Content-Type", "application/json")
      .asJSON
      .check(status is 200)
  )

  val listTasks = exec(
    http("List tasks")
      .get("/tasks")
      .header("Content-Type", "application/json")
      .asJSON
      .check(status is 200)
  )
}

/**
 * This flow attempts to simulate common user behavior:
 *
 * - He creates a task.
 * - He want to see the details of the task just created.
 * - He waits and sees that it needs an update.
 * - He lists all tasks to see how they all look.
 * - He waits and decides to delete it.
 */
class TaskCommonFlowTest extends TaskLoadTest {

  def scenarioName = "CRUD operations and fetching tasks"

  val taskFlowScenario = scenario(scenarioName)
    .exec(
      createTask   pause 2,
      retrieveTask pause 2,
      updateTask   pause 3,
      listTasks    pause 3,
      deleteTask   pause 2
    )

  setUp(
    taskFlowScenario.inject(
      atOnceUsers(25),
      rampUsers(75) over (10 seconds)
    )
  ) maxDuration 5000 protocols httpProtocol
}