package com.whiteprompt.services

import java.util.UUID

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import com.whiteprompt.domain.{Task, TaskEntity}
import com.whiteprompt.persistence.TaskRepository

import scala.concurrent.Future

class TaskService(val taskRepository: TaskRepository) extends Actor {
  import TaskService._
  implicit val ec = context.dispatcher

  override def receive: Receive = {
    case CreateTask(task) => {
      def create(task: Task): Future[TaskEntity] = {
        val id = UUID.randomUUID()
        taskRepository.create(TaskEntity(id, task.name, task.description))
      }

      create(task) pipeTo sender
    }

    case UpdateTask(id, task) => {
      def update(id: UUID, toUpdate: Task): Future[Option[TaskEntity]] = {
        taskRepository.update(TaskEntity(id, toUpdate.name, toUpdate.description))
      }

      update(id, task) pipeTo sender
    }

    case RetrieveTask(taskId) => {
      def retrieve(id: UUID): Future[Option[TaskEntity]] = {
        taskRepository.retrieve(id)
      }

      retrieve(taskId) pipeTo sender
    }

    case DeleteTask(taskId) => {
      def delete(id: UUID): Future[Option[TaskEntity]] = {
        taskRepository.delete(id)
      }

      delete(taskId) pipeTo sender
    }

    case ListTasks => {
      def list(): Future[Seq[TaskEntity]] = {
        taskRepository.list()
      }

      list pipeTo sender
    }
  }

}

object TaskService {

  val Name = "task-service"

  def props(taskRepository: TaskRepository): Props = {
    Props(classOf[TaskService], taskRepository)
  }

  case class CreateTask(task: Task)
  case class RetrieveTask(taskId: UUID)
  case class UpdateTask(taskId: UUID, task: Task)
  case class DeleteTask(taskId: UUID)
  case object ListTasks
}