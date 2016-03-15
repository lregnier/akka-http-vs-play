package com.github.frossi85.stress_tests

import com.github.frossi85.database.{Repository, TestDB}
import com.github.frossi85.domain.Task
import com.github.frossi85.services.TaskServiceInterface
import com.google.inject.Injector
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import net.codingwell.scalaguice.InjectorExtensions._
import scala.concurrent.duration._

class TasksLoadTestDBSimulation extends Simulation with TestDB {
  val host = "localhost"
  val port = 8085

  val server = GatlingAkkaHttpServer(host, port)
  val injector: Injector = server.injector

  override def repository: Repository[Task] = injector.instance[TaskServiceInterface].asInstanceOf[Repository[Task]]

  initializeRepository()
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