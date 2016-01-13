package com.github.frossi85.stress_tests

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class TasksLoadTestSimulation extends Simulation {
  //I need to create an akka http test server to use gatling like a test

  val scenarioName = "CreateUpdateListViewTasks"

	val httpProtocol = http
		.baseURL("http://localhost:8080/v1")

	//val headers_0 = Map("X-Client-Data" -> "CKW2yQEI/ZXKAQ==")

	//val headers_1 = Map("Pragma" -> "no-cache")

	val scn = scenario(scenarioName)
		.exec(
      TaskTests.createTask,
      TaskTests.viewTask,
      TaskTests.updateTask,
      TaskTests.listTasks,
      TaskTests.deleteTask
    )
		//.pause(59)

	setUp(scn.inject(rampUsers(10) over (10 seconds))).protocols(httpProtocol)
}