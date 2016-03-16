package com.whiteprompt.api

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.google.inject.{Guice, Injector}
import com.whiteprompt.{ConfigModule, ServicesModule}
import com.whiteprompt.database.TestDB
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
    new ServicesModule()
  )

  override protected def beforeEach() {
    super.beforeEach()
    initializeRepository()
    Kamon.start()
  }

  override protected def afterEach() {
    cleanUpRepository()
    Kamon.shutdown()
    super.afterEach()
  }
}
