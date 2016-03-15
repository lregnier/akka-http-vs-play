import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class TasksLoadTestDBSimulation extends Simulation {
  val host = "localhost"
  val port = 9000

  val scenarioName = "CreateUpdateListViewTasks"

	val httpProtocol = http
		.baseURL(s"http://$host:$port/v1")

	val scn = scenario(scenarioName)
		.exec(
      TaskTests.createTask,
      TaskTests.viewTask,
      TaskTests.updateTask,
      TaskTests.listTasks,
      TaskTests.deleteTask
    )

	setUp(scn.inject(rampUsers(10) over (10 seconds))).protocols(httpProtocol)
}