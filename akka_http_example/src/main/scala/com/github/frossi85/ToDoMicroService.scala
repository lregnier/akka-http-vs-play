package com.github.frossi85

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.github.frossi85.api.Endpoints
import com.google.inject.{Guice, Injector}
import scala.io.StdIn

object ToDoMicroService extends App with KamonHandler with Endpoints {
  implicit val system: ActorSystem = ActorSystem("todo-microservice-actor-system")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val injector: Injector = Guice.createInjector(
    new ConfigModule(),
    new ServicesModule()
  )

  val bindingFuture = Http().bindAndHandle(routes, "0.0.0.0", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // for the future transformations
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => {
      stopKamon()
      system.terminate()
      System.exit(0)
    }) // and shutdown when done

}

