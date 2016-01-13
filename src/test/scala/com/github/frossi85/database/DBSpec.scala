package com.github.frossi85.database

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

abstract class DBSpec extends FunSuite
  with BeforeAndAfterEach
  with ScalaFutures
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





