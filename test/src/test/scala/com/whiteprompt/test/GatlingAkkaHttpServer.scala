package com.whiteprompt.test

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import com.google.inject.{Guice, Injector}
import com.whiteprompt.api.Endpoints
import com.whiteprompt.{ConfigModule, ServicesModule}
import kamon.Kamon

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

case class GatlingAkkaHttpServer(host: String, port: Int) extends Endpoints {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val injector: Injector = Guice.createInjector(
    new ConfigModule(),
    new ServicesModule()
  )

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
