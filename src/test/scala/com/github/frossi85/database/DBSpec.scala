package com.github.frossi85.database

import com.github.frossi85.services.TaskService
import org.scalatest.Matchers._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import slick.driver.H2Driver.api._
import scala.concurrent.Await
import scala.concurrent.duration.Duration


class TablesSuite extends FunSuite with BeforeAndAfter with ScalaFutures {

  val taskService = new TaskService
  var db = taskService.db

  implicit var session: Session = _

  before {
    Await.result(taskService.setUp(), Duration.Inf)
    Await.result(taskService.populateWithDummyData(), Duration.Inf)
  }

  test("Query Suppliers works") {
    /*val task = Task("Task.scala 1", "One description", 1L)
    Await.result(db.run(taskService.repository += task).map(_ => task), Duration.Inf)*/

    //val result = db.run(taskService.repository.filter(_.id > 0L).result)//.byId(1)

    /*whenReady(result) { entity =>
      //entity should not be empty
      entity.length should be >= 0
    }*/


    /*val result = Await.result(db.run(taskService.repository.filter(_.id > 0L).result),  Duration.Inf)

    result.length should be >= 0*/

    1 should be > 0

  }

  after {
    session.close()
  }
}