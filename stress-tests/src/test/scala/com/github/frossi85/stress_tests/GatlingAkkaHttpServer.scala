package com.github.frossi85.stress_tests

import _root_.com.github.frossi85.Routes
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import kamon.Kamon
import slick.jdbc.JdbcBackend
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

case class GatlingAkkaHttpServer(database: JdbcBackend#Database, host: String, port: Int) extends Routes {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  def getDatabase: JdbcBackend#Database = database

  private var bindingFuture: Option[Future[ServerBinding]] = None

  def start() = {
    Kamon.start()

    val server = Http().bindAndHandle(routes, host, port)
    Await.result(server, Duration.Inf)
    bindingFuture = Some(server)
  }

  def shutdown() = bindingFuture.get.flatMap(_.unbind())
    .onComplete(_ => {
      system.terminate()
      Kamon.shutdown()
    })
}
