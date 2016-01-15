package com.github.frossi85.stress_tests

import _root_.com.github.frossi85.DBTest
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class TasksLoadTestSimulation extends Simulation with DBTest {
  initializeDatabase()

  val server = GatlingAkkaHttpServer(getDatabase, "localhost", 8080)
  server.start()

  val scenarioName = "CreateUpdateListViewTasks"

	val httpProtocol = http
		.baseURL("http://localhost:8080/v1")

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