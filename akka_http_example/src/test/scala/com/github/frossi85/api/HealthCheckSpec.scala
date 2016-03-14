package com.github.frossi85.api

import com.github.frossi85.database.Repository
import com.github.frossi85.domain.Task
import com.github.frossi85.services.TaskServiceInterface
import net.codingwell.scalaguice.InjectorExtensions._

class HealthCheckSpec extends ApiSpec with Endpoints {
  val repository: Repository[Task] = injector.instance[TaskServiceInterface].asInstanceOf[Repository[Task]]

  "The service" should {

    "return a greeting for GET requests to the root path 222222" in {
      // tests:
      Get("/health-check") ~> routes ~> check {
        responseAs[String] shouldEqual "It's Alive"
      }
    }
  }

}