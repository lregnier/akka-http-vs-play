package com.whiteprompt.services

import com.whiteprompt.domain.Task

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TaskActorActions {
  val taskService: TaskServiceInterface

  def list: Future[Seq[Task]] = {
    taskService.all
  }

  def get(taskId: Long): Future[Option[Task]] = {
    taskService.byId(taskId)
  }

  def create(request: TaskRequest): Future[Task] = {
    taskService.insert(Task(request.name, request.description))
  }

  def update(taskId: Long, request: TaskRequest): Future[Option[Task]] = {
    taskService.byId(taskId).flatMap {
      case Some(task) => taskService.update(task.copy(name = request.name, description = request.description)).map(x => Some(x))
      case None => Future(None)
    }
  }

  def delete(taskId: Long): Future[Long] = {
      taskService.delete(taskId)
  }
}
