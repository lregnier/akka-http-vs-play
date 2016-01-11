package com.github.frossi85.database

import com.github.frossi85.database.DBSpec
import com.github.frossi85.domain.Task
import com.github.frossi85.services.TaskService
import org.scalatest.FunSuite
import org.scalatest.Matchers._
import slick.driver.H2Driver.api._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class TaskServiceSuite extends DBSpec {
  val taskService = new TaskService

  test("Get task by id must return non empty value") {
    val result = taskService.byId(1)

    whenReady(result) { entity =>
      entity should not be empty
    }
  }

  test("Insert new task must increment the count") {
    val oldCount = Await.result(db.run(taskService.repository.length.result), Duration.Inf)
    val name = "new task"
    val description = "description"
    val userId = 1L
    val result = Await.result(taskService.insert(Task(name, description, userId)), Duration.Inf)
    val newCount = Await.result(db.run(taskService.repository.length.result), Duration.Inf)

    newCount should equal (oldCount + 1)
    result.id should be > 0L
    result.name should equal (name)
    result.description should equal (description)
    result.userId should equal (userId)
  }

  test("Update exiting entity must update the fields") {
    val name = "updated task"
    val description = "updated description"
    val userId = 1L

    Await.result(taskService.update(Task(name, description, userId, 1L)), Duration.Inf)

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
    val oldCount = Await.result(db.run(taskService.repository.length.result), Duration.Inf)

    Await.result(taskService.delete(1L), Duration.Inf)

    val newCount = Await.result(db.run(taskService.repository.length.result), Duration.Inf)
    val entity = Await.result(taskService.byId(1), Duration.Inf)

    newCount should equal (oldCount - 1)
    entity shouldBe empty
  }
}