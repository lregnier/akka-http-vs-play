package com.whiteprompt.persistence

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.whiteprompt.domain.{TaskEntity, Task}
import com.whiteprompt.services.TaskServiceActor
import com.whiteprompt.services.TaskServiceActor._
import kamon.Kamon
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import scala.concurrent.Await
import scala.concurrent.duration._

class TaskServiceSuite extends WordSpec with Matchers with ScalaFutures {

  Kamon.start()

  trait Context {
    implicit val system = ActorSystem("my-actor-system")
    implicit val timeout = Timeout(5 seconds)

    val taskService = system.actorOf(TaskServiceActor.props())

    val task1 = Await.result(
      (taskService ? CreateTask(task("Task.scala 1", "One description")))
        .mapTo[TaskEntity]
      , Duration.Inf)

    val task2 = Await.result(
      (taskService ? CreateTask(task("Task.scala 2", "Another description")))
        .mapTo[TaskEntity]
      , Duration.Inf)

    def task(_name: String, _description: String) = new Task {
      val name = _name
      val description = _description
    }

    def repositoryItems: Seq[TaskEntity] = Await.result((taskService ? ListTasks).mapTo[Seq[TaskEntity]], Duration.Inf)
  }

  "The service" should {
    "create a task and increment the count" in new Context {
      val oldCount = repositoryItems.size
      val name = "new task"
      val description = "description"
      val result = (taskService ? CreateTask(task(name, description))).mapTo[TaskEntity]

      whenReady(result) { entity =>
        val newCount = repositoryItems.size

        newCount shouldEqual oldCount + 1
        entity.id should be > 0L
        entity.name shouldEqual name
        entity.description shouldEqual description
      }
    }

    "get a task by id and the returned value is no empty" in new Context {
      val result = (taskService ? RetrieveTask(task1.id)).mapTo[Option[TaskEntity]]

      whenReady(result) { entity =>
        entity shouldBe Some(task1)
      }
    }

    "update existing task with new field values" in new Context {
      val name = "updated task"
      val description = "updated description"
      val result = (taskService ? UpdateTask(task1.id, task(name, description))).mapTo[Option[TaskEntity]]

      whenReady(result) { entity =>
        val updatedTask = repositoryItems.find(x => x.id == task1.id)

        updatedTask should not be empty
        updatedTask.map(x => {
          x.name shouldEqual name
          x.description shouldEqual description
        })
      }
    }

    "delete a task decrement the count" in new Context {
      val oldCount = repositoryItems.size
      val result = (taskService ? DeleteTask(task1.id)).mapTo[Option[TaskEntity]]

      whenReady(result) { entity =>
        val newCount = repositoryItems.size
        val entity = repositoryItems.find(x => x.id == task1.id)

        newCount shouldEqual oldCount - 1
        entity shouldBe empty
      }

    }

    "return the list of tasks for GET request to /tasks path" in new Context {
      val result = (taskService ? ListTasks).mapTo[Seq[TaskEntity]]

      whenReady(result) { entities =>
        entities shouldEqual List(task1, task2)
      }
    }
  }
}