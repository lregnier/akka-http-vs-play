package com.github.frossi85.api

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.frossi85.database.TestDB
import kamon.Kamon
import org.scalatest._

abstract class ApiSpec extends WordSpec
  with Matchers
  with ScalatestRouteTest
  with BeforeAndAfterEach
  with TestDB
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
