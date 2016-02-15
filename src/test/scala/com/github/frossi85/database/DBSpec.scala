package com.github.frossi85.database

import com.github.frossi85.DBTest
import kamon.Kamon
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

abstract class DBSpec extends FunSuite
  with BeforeAndAfterEach
  with ScalaFutures
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





