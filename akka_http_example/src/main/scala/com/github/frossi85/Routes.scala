package com.github.frossi85

import akka.http.scaladsl.server.Directives._
import com.github.frossi85.api.TasksApi
import com.github.frossi85.database.DB

trait Routes extends TasksApi {
  lazy val routes = pathPrefix("v1") {
    tasksRoutes
  } ~
  path("health-check") {
    get {
      complete("It's Alive")
    }
  } ~
  path("create-schemas") {
    post {
      complete {
        DB.createSchemas()
        "Schemas created"
      }
    }
  } ~
  path("populate-database") {
    post {
      complete {
        DB.populateWithDummyData()
        "Database populated with dummy data"
      }
    }
  }
}