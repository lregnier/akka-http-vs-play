package com.whiteprompt.database

import com.whiteprompt.domain.Task
import com.whiteprompt.services.TaskServiceInterface
import net.codingwell.scalaguice.InjectorExtensions._
import org.scalatest.Matchers._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class TaskServiceSuite extends SpecDB {
  val taskService: TaskServiceInterface = injector.instance[TaskServiceInterface]
  val repository: Repository[Task] = injector.instance[Repository[Task]]

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
    val oldCount = repository.store.size
    val name = "new task"
    val description = "description"
    val result = Await.result(taskService.insert(Task(name, description)), Duration.Inf)
    val newCount = repository.store.size

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
    val oldCount = repository.store.size

    Await.result(taskService.delete(1L), Duration.Inf)

    val newCount = repository.store.size
    val entity = Await.result(taskService.byId(1), Duration.Inf)

    newCount should equal (oldCount - 1)
    entity shouldBe empty
  }
}