package com.github.frossi85.database

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Await
import scala.concurrent.duration.Duration

abstract class DBSpec extends FunSuite with BeforeAndAfter with ScalaFutures {
  val db = DB.db

  before {
    Await.result(DB.createSchemas(), Duration.Inf)
    Await.result(DB.populateWithDummyData(), Duration.Inf)
  }

  after {
    Await.result(DB.dropSchemas(), Duration.Inf)
  }
}




