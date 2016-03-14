package com.github.frossi85.api

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.frossi85.{TestDatabaseModule, ServicesModule, ConfigModule}
import com.github.frossi85.database.TestDB
import com.google.inject.{Guice, Injector}
import kamon.Kamon
import org.scalatest._

abstract class ApiSpec extends WordSpec
  with Matchers
  with ScalatestRouteTest
  with BeforeAndAfterEach
  with TestDB
{
  val injector: Injector = Guice.createInjector(
    new ConfigModule(),
    new TestDatabaseModule(),
    new ServicesModule()
  )

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
