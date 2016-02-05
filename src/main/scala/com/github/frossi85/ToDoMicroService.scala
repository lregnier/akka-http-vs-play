package com.github.frossi85

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.github.frossi85.database.DB
import kamon.Kamon
import slick.jdbc.JdbcBackend

object ToDoMicroService extends App with Routes {
  Kamon.start()

  implicit val system = ActorSystem("todo-microservice-actor-system")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  def getDatabase: JdbcBackend#Database = DB.db

  val bindingFuture = Http().bindAndHandle(routes, "0.0.0.0", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  Console.readLine() // for the future transformations
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => {
      system.terminate()
      Kamon.shutdown()
    }) // and shutdown when done
}
