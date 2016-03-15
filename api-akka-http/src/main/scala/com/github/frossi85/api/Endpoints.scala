package com.github.frossi85.api

import akka.http.scaladsl.server.Directives._

trait Endpoints extends TasksApi {
  lazy val routes = pathPrefix("v1") {
    tasksRoutes
  } ~
  path("health-check") {
    get {
      complete("It's Alive")
    }
  }
}