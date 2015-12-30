package com.github.frossi85

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.frossi85.database.DB
import org.scalatest._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

abstract class ApiSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter {
  val db = DB.db

  before {
    //Await.result(DB.createSchemas(), Duration.Inf)
    //Await.result(DB.populateWithDummyData(), Duration.Inf)
  }

  after {
    //Await.result(DB.dropSchemas(), Duration.Inf)
  }
}
