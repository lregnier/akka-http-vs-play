package com.whiteprompt.services

import java.util.UUID

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import com.whiteprompt.domain.Task
import com.whiteprompt.persistence.TaskRepository

class TaskServiceActor(val taskRepository: TaskRepository) extends Actor with TaskService {
  import TaskServiceActor._
  implicit val ec = context.dispatcher

  override def receive: Receive = {
    case CreateTask(task) => {
      create(task) pipeTo sender
    }
    case UpdateTask(id, task) => {
      update(id, task) pipeTo sender
    }
    case RetrieveTask(taskId) => {
      retrieve(taskId) pipeTo sender
    }
    case DeleteTask(taskId) => {
      delete(taskId) pipeTo sender
    }
    case ListTasks => {
      list pipeTo sender
    }
  }
}

object TaskServiceActor {

  val Name = "task-service"

  def props(taskRepository: TaskRepository): Props = {
    Props(classOf[TaskServiceActor], taskRepository)
  }

  case class CreateTask(task: Task)
  case class RetrieveTask(taskId: UUID)
  case class UpdateTask(taskId: UUID, task: Task)
  case class DeleteTask(taskId: UUID)
  object ListTasks
}



