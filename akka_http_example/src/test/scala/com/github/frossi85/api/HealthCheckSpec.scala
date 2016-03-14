package com.github.frossi85.api

class HealthCheckSpec extends ApiSpec with Endpoints {
  "The service" should {

    "return a greeting for GET requests to the root path 222222" in {
      // tests:
      Get("/health-check") ~> routes ~> check {
        responseAs[String] shouldEqual "It's Alive"
      }
    }
  }
}