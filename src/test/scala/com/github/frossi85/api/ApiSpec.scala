package com.github.frossi85.api

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.frossi85.DBTest
import org.scalatest._

abstract class ApiSpec extends WordSpec
  with Matchers
  with ScalatestRouteTest
  with BeforeAndAfterEach
  with BeforeAndAfterAll
  with DBTest
{
  override protected def beforeEach() {
    super.beforeEach()
    initializeDatabase()
  }

  override protected def afterEach() {
    shutdownDatabase()
    super.afterEach()
  }
}
