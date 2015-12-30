package com.github.frossi85

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.frossi85.database.DB
import org.scalatest._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

abstract class ApiSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterEach with BeforeAndAfterAll {
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
    //Await.result(DB.dropSchemas(), Duration.Inf)
    super.afterEach()
  }
}
