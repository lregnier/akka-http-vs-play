package com.whiteprompt.services

import akka.actor.ActorSystem
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import com.whiteprompt.TestData
import com.whiteprompt.domain.{Task, TaskEntity}
import com.whiteprompt.services.TaskServiceActor._
import org.scalatest.{Suite, BeforeAndAfterAll, Matchers, WordSpecLike}

class TaskServiceSpec extends TestKit(ActorSystem("TaskServiceSpec"))
    with DefaultTimeout with ImplicitSender with WordSpecLike with Matchers with StopSystemAfterAll {

  trait Scope extends TestData {
    implicit val context = system.dispatcher
    val taskService = system.actorOf(TaskServiceActor.props(taskRepository()))

    def task(_name: String, _description: String): Task = new Task {
      val name = _name
      val description = _description
    }
  }

  "When sending a CreateTask msg, the service" should {
    "respond with the created Task if no errors" in new Scope {
      val name = "Create name"
      val description = "Create description"
      taskService ! CreateTask(task(name, description))
      expectMsgPF() {
        case TaskEntity(_, createdName, createdDescription) =>
          createdName shouldEqual name
          createdDescription shouldEqual description
      }
    }
  }

  "When sending a RetrieveTask msg, the service" should {
    "respond with the requested Task if it exists" in new Scope {
      val id = taskEntity1.id
      taskService ! RetrieveTask(id)
      expectMsgPF() {
        case Some(TaskEntity(requestedId, _, _)) =>
          requestedId shouldEqual id
      }
    }
    "respond with None if the requested Task does not exist" in new Scope {
      taskService ! RetrieveTask(nonExistentTaskId)
      expectMsg(None)
    }
  }

  "When sending an UpdateTask msg, the service" should {
    "respond with the updated Task if it exists" in new Scope {
      val id = taskEntity1.id
      val name = "Update name"
      val description = "Update description"
      taskService ! UpdateTask(id, task(name, description))
      expectMsg(Some(TaskEntity(id, name, description)))
    }
    "respond with None if the requested Task does not exist" in new Scope {
      taskService ! UpdateTask(nonExistentTaskId, task("Update name", "Update description"))
      expectMsg(None)
    }
  }

  "When sending a DeleteTask msg, the service" should {
    "respond with the deleted Task if it exists" in new Scope {
      val id = taskEntity1.id
      taskService ! DeleteTask(id)
      expectMsgPF() {
        case Some(TaskEntity(deletedId, _, _)) =>
          deletedId shouldEqual id
      }
    }
    "respond with None if the requested Task does not exist" in new Scope {
      taskService ! DeleteTask(nonExistentTaskId)
      expectMsg(None)
    }
  }

  "When sending a ListTasks msg, the service" should {
    "respond with all the Tasks present in the repository" in new Scope {
      taskService ! ListTasks
      expectMsgPF() {
        case tasks: Seq[TaskEntity] =>
          tasks should contain theSameElementsAs(Seq(taskEntity1, taskEntity2))
      }
    }
  }

}

trait StopSystemAfterAll extends BeforeAndAfterAll {
   self: TestKit with Suite =>

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
}