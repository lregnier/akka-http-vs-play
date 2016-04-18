package com.whiteprompt

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.whiteprompt.api.Routes
import com.whiteprompt.conf.Config
import com.whiteprompt.persistence.TaskRepository
import com.whiteprompt.services.TaskServiceActor
import kamon.Kamon

import scala.io.StdIn

object Main extends App with Config with KamonHandler with Routes {
  implicit val system = ActorSystem("api-akka-http-system")

  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  val log = Logging(system, getClass)

  // Initialize service actor
  val taskService = system.actorOf(TaskServiceActor.props(TaskRepository()), "task-service")

  val serverBinding = Http().bindAndHandle(routes, httpInterface, httpPort)

  StdIn.readLine() // for the future transformations
  serverBinding
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => {
      stopKamon()
      system.terminate()
      System.exit(0)
    }) // and shutdown when done
}

trait KamonHandler {
  Kamon.start()

  def stopKamon() = {
    Kamon.shutdown()
  }
}

