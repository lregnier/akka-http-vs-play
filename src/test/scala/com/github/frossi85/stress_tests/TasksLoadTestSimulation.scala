package com.github.frossi85.stress_tests

import _root_.com.github.frossi85.{DBTest, Routes}
import _root_.com.github.frossi85.database.DB
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.ActorMaterializer
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import slick.jdbc.JdbcBackend
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._


case class GatlingAkkaHttpServer(database: JdbcBackend#Database, port: Int) extends Routes {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  def getDatabase: JdbcBackend#Database = database

  var bindingFuture: Option[Future[ServerBinding]] = None

  def start() = {
    bindingFuture = Some(Http().bindAndHandle(routes, "localhost", port))
    bindingFuture
  }

  def shutdown() = bindingFuture.get.flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}

class TasksLoadTestSimulation extends Simulation with DBTest {

  initializeDatabase()

  val bindingFuture = GatlingAkkaHttpServer(getDatabase, 8080).start()

  Await.result(bindingFuture.get, Duration.Inf)


  before(() => {
    /*val server = GatlingAkkaHttpServer(8080)
    app = Some(server)
    Await.result(server.bindingFuture, Duration.Inf)*/
  })

  after(() => {
    /*app.map(_.shutdown())*/
    //shutdownDatabase()
  })

  //I need to create an akka http test server to use gatling like a test

  val scenarioName = "CreateUpdateListViewTasks"

	val httpProtocol = http
		.baseURL("http://localhost:8080/v1")

	//val headers_0 = Map("X-Client-Data" -> "CKW2yQEI/ZXKAQ==")

	//val headers_1 = Map("Pragma" -> "no-cache")

	val scn = scenario(scenarioName)
		.exec(
      TaskTests.createTask,
      TaskTests.viewTask,
      TaskTests.updateTask,
      TaskTests.listTasks,
      TaskTests.deleteTask
    )
		//.pause(59)

	setUp(scn.inject(rampUsers(10) over (10 seconds))).protocols(httpProtocol)
}