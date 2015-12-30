package com.github.frossi85.database

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Await
import scala.concurrent.duration.Duration

abstract class DBSpec extends FunSuite with BeforeAndAfterEach with BeforeAndAfterAll with ScalaFutures {
  val db = DB.db

  override protected def beforeEach() {
    super.beforeEach()

    Await.result(DB.createSchemas(), Duration.Inf)
    Await.result(DB.populateWithDummyData(), Duration.Inf)
  }

  override protected def afterEach() {
    Await.result(DB.dropSchemas(), Duration.Inf)
    super.afterEach()
  }

  override protected def afterAll() {
   // Await.result(DB.dropSchemas(), Duration.Inf)
    super.afterEach()
  }
}




