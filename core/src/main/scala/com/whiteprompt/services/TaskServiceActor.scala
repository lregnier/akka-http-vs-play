package com.whiteprompt.services

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import com.whiteprompt.domain.Task
import com.whiteprompt.persistence.TaskRepository
import kamon.trace.Tracer

class TaskServiceActor(val taskRepository: TaskRepository) extends Actor with TaskService {
  import TaskServiceActor._
  implicit val ec = context.dispatcher

  override def receive: Receive = {
    case CreateTask(task) => {
      Tracer.withNewContext("Create Task", autoFinish = true) {
        create(task) pipeTo sender
      }
    }
    case UpdateTask(id, task) => {
      Tracer.withNewContext("Update Task", autoFinish = true) {
        update(id, task) pipeTo sender
      }
    }
    case RetrieveTask(taskId) => {
      Tracer.withNewContext("Retrieve Task", autoFinish = true) {
        retrieve(taskId) pipeTo sender
      }
    }
    case DeleteTask(taskId) => {
      Tracer.withNewContext("Delete Task", autoFinish = true) {
        delete(taskId) pipeTo sender
      }
    }
    case ListTasks => {
      Tracer.withNewContext("List Tasks", autoFinish = true) {
        list pipeTo sender
      }
    }
  }
}

object TaskServiceActor {
  def props(taskRepository: TaskRepository): Props = {
    Props(classOf[TaskServiceActor], taskRepository)
  }

  case class CreateTask(task: Task)
  case class RetrieveTask(taskId: Long)
  case class UpdateTask(taskId: Long, task: Task)
  case class DeleteTask(taskId: Long)
  object ListTasks
}



