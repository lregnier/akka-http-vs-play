package frossi85.database

import com.github.frossi85.database.DB
import com.github.frossi85.database.tables.AgnosticDriver.api._
import com.github.frossi85.domain.Task
import com.github.frossi85.services.{TaskServiceInterface, TaskService}
import org.scalatest.Matchers._
import slick.jdbc.JdbcBackend
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import com.github.frossi85.domain.Task
import com.github.frossi85.services.{TaskActor, TaskServiceInterface, _}
import com.google.inject.Injector
import kamon.trace.Tracer
import net.codingwell.scalaguice.InjectorExtensions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class TaskServiceSuite extends SpecDB {
  val taskService: TaskServiceInterface = injector.instance[TaskServiceInterface]

  test("Get task by id must return non empty value") {
    val result = taskService.byId(1)

    whenReady(result) { entity =>
      entity should not be empty
    }
  }

  test("Get tasks must return non empty value") {
    val result = taskService.all

    whenReady(result) { entity =>
      entity should not be empty
    }
  }

  test("Insert new task must increment the count") {
    val oldCount = Await.result(db.run(DB.tasks.length.result), Duration.Inf)
    val name = "new task"
    val description = "description"
    val result = Await.result(taskService.insert(Task(name, description)), Duration.Inf)
    val newCount = Await.result(db.run(DB.tasks.length.result), Duration.Inf)

    newCount should equal (oldCount + 1)
    result.id should be > 0L
    result.name should equal (name)
    result.description should equal (description)
  }

  test("Update exiting entity must update the fields") {
    val name = "updated task"
    val description = "updated description"

    Await.result(taskService.update(Task(name, description, 1L)), Duration.Inf)

    val result = taskService.byId(1)

    whenReady(result) { entity =>
      entity should not be empty
      entity.map(x => {
        x.name should equal(name)
        x.description should equal(description)
      })
    }
  }

  test("Delete exiting entity must decrement the count") {
    val oldCount = Await.result(db.run(DB.tasks.length.result), Duration.Inf)

    Await.result(taskService.delete(1L), Duration.Inf)

    val newCount = Await.result(db.run(DB.tasks.length.result), Duration.Inf)
    val entity = Await.result(taskService.byId(1), Duration.Inf)

    newCount should equal (oldCount - 1)
    entity shouldBe empty
  }
}