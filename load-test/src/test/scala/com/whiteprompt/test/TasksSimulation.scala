package com.whiteprompt.test

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

/**
 * This object provides the scenario parts of the load tests.
 */
object TaskRequests {

  def taskData(): String =  {
    val randomString = Random.alphanumeric.take(10).mkString
    s"""{
      "name": "$randomString",
      "description": "$randomString"
    }"""
  }

  val lastUrlSegmentFromLocationHeader = headerRegex("Location", "[^/]+(?=/$|$)").ofType[String]

  val createTask = exec(
    http("Create a task")
      .post("/tasks")
      .header("Content-Type", "application/json")
      .body(StringBody(taskData))
      .check(lastUrlSegmentFromLocationHeader.saveAs("taskId"))
      .check(status is 201)
  )

  val retrieveTask = exec(
    http("Retrieve a task")
      .get("/tasks/${taskId}")
      .header("Accept", "application/json")
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
      .check(status is 204)
  )

  val listTasks = exec(
    http("List tasks")
      .get("/tasks")
      .header("Accept", "application/json")
      .check(status is 200)
  )
}

object Create {
  import TaskRequests._

  val create =
    exec(
      listTasks    pause 3, // This step is for setting up the update
      createTask   pause 5,
      listTasks    pause 3,
      retrieveTask pause 5
    )
}

object Update {
  import TaskRequests._

  val update =
    exec(
      createTask   pause 1, // This step is for setting up the update
      listTasks    pause 3,
      retrieveTask pause 5,
      updateTask   pause 5
    )
}

object Delete {
  import TaskRequests._

  val delete =
    exec(
      createTask   pause 1, // This step is for setting up the update
      listTasks    pause 3,
      retrieveTask pause 5,
      deleteTask   pause 5
    )
}

/**
 * This flow attempts to simulate common user behavior:
 */
class TasksSimulation extends Simulation with Config {

  val apiVersion = "v1"
  val httpProtocol = http.baseURL(s"http://$httpInterface:$httpPort/$apiVersion")

  val scenarioName = "CRUD operations and fetching tasks"

  val taskFlowScenario = scenario(scenarioName)
    .exec(
      Create.create,
      Update.update,
      Delete.delete
    )

  setUp(
    taskFlowScenario.inject(
      atOnceUsers(100),
      rampUsers(150) over (15 seconds)
    )
  ) maxDuration 5000 protocols httpProtocol
}