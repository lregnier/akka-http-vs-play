package com.whiteprompt.persistence

import com.google.inject.{Guice, Injector}
import com.whiteprompt.ConfigModule
import kamon.Kamon
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

abstract class SpecDB extends FunSuite
  with BeforeAndAfterEach
  with ScalaFutures
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
    Kamon.shutdown()
    cleanUpRepository()
    super.afterEach()
  }
}





