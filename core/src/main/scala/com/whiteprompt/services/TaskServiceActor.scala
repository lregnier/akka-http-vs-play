package com.whiteprompt.services

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import com.whiteprompt.domain.TaskRequest
import com.whiteprompt.persistence.TaskRepository
import kamon.trace.Tracer

class TaskServiceActor extends Actor with TaskService {

  import TaskServiceActor._
  implicit val executionContext = context.dispatcher

  val taskRepository = TaskRepository()

  override def receive: Receive = {
    case CreateTask(request) => {
      Tracer.withNewContext("CreateTaskFromRequest", autoFinish = true) {
        create(request) pipeTo sender
      }
    }
    case UpdateTask(id, toUpdate) => {
      Tracer.withNewContext("UpdateTaskFromRequest", autoFinish = true) {
        update(id, toUpdate) pipeTo sender
      }
    }
    case RetrieveTask(taskId) => {
      Tracer.withNewContext("GetTaskById", autoFinish = true) {
        retrieve(taskId) pipeTo sender
      }
    }
    case DeleteTask(taskId) => {
      Tracer.withNewContext("DeleteTaskById", autoFinish = true) {
        delete(taskId) pipeTo sender
      }
    }
    case ListTasks => {
      Tracer.withNewContext("GetTasksByUserId", autoFinish = true) {
        list pipeTo sender
      }
    }
  }
}

object TaskServiceActor {
  def props(): Props = Props[TaskServiceActor]

  case class CreateTask(request: TaskRequest)
  case class RetrieveTask(taskId: Long)
  case class UpdateTask(taskId: Long, request: TaskRequest)
  case class DeleteTask(taskId: Long)
  object ListTasks
}



