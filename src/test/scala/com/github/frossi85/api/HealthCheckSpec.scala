package com.github.frossi85.api

import com.github.frossi85.Routes
import com.github.frossi85.database.tables.AgnosticDriver.api._
import slick.jdbc.JdbcBackend

class HealthCheckSpec extends ApiSpec with Routes {
  "The service" should {

    "return a greeting for GET requests to the root path 222222" in {
      // tests:
      Get("/health-check") ~> routes ~> check {
        responseAs[String] shouldEqual "It's Alive"
      }
    }
  }
}
