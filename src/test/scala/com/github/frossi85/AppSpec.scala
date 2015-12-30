package com.github.frossi85

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.frossi85.api.TasksApi
import com.github.frossi85.database.DB
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class FullTestKitExampleSpec extends ApiSpec with TasksApi {

  val smallRoute =
    get {
      pathSingleSlash {
        complete {
          "Captain on the bridge!"
        }
      } ~
        path("ping") {
          complete("PONG!")
        }
    }

  "The service" should {

    "return a greeting for GET requests to the root path 222222" in {
      // tests:
      Get("/health-check") ~> tasksRoutes ~> check {
        responseAs[String] shouldEqual "It's Alive"
      }
    }

    "return a greeting for GET requests to the root path" in {
      // tests:
      Get() ~> smallRoute ~> check {
        responseAs[String] shouldEqual "Captain on the bridge!"
      }
    }

    "return a 'PONG!' response for GET requests to /ping" in {
      // tests:
      Get("/ping") ~> smallRoute ~> check {
        responseAs[String] shouldEqual "PONG!"
      }
    }

    "leave GET requests to other paths unhandled" in {
      // tests:
      Get("/kermit") ~> smallRoute ~> check {
        handled shouldBe false
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      // tests:
      Put() ~> Route.seal(smallRoute) ~> check {
        status === StatusCodes.MethodNotAllowed
        responseAs[String] shouldEqual "HTTP method not allowed, supported methods: GET"
      }
    }
  }
}