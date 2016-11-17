package com.whiteprompt

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.routing.FromConfig
import akka.stream.ActorMaterializer
import com.whiteprompt.api.Routes
import com.whiteprompt.conf.Config
import com.whiteprompt.persistence.TaskRepository
import com.whiteprompt.services.TaskServiceActor

object Main extends App with Config with Routes {
  implicit val system = ActorSystem("api-akka-http-system")
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  val log = Logging(system, getClass)

  // Services
  val taskService = system.actorOf(FromConfig.props(TaskServiceActor.props(TaskRepository())), "task-service")

  // Initialize server
  Http().bindAndHandle(routes, httpInterface, httpPort)

}

