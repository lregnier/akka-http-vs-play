package com.github.frossi85.api

import akka.http.scaladsl.testkit.ScalatestRouteTest
import kamon.Kamon
import org.scalatest._
import com.github.frossi85.test.DBTest

abstract class ApiSpec extends WordSpec
  with Matchers
  with ScalatestRouteTest
  with BeforeAndAfterEach
  with DBTest
{
  override protected def beforeEach() {
    super.beforeEach()
    Kamon.start()
    initializeDatabase()
  }

  override protected def afterEach() {
    shutdownDatabase()
    Kamon.shutdown()
    super.afterEach()
  }
}
