package com.whiteprompt.api

import akka.http.scaladsl.server.Directives._

trait Routes extends TasksRoutes {

  val routes =
    pathPrefix("v1") {
      tasksRoutes
    } ~
    path("health-check"){
      get {
        complete("It's Alive")
      }
    }
}