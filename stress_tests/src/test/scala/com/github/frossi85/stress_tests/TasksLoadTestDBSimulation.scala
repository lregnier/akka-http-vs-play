package com.github.frossi85.stress_tests

import com.github.frossi85.database.TestDB
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class TasksLoadTestDBSimulation extends Simulation with TestDB {
  initializeDatabase()

  val host = "localhost"
  val port = 8085

  val server = GatlingAkkaHttpServer(getDatabase, host, port)
  server.start()

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