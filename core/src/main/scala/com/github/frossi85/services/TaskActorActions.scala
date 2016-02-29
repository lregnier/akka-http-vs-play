package com.github.frossi85.services

import com.github.frossi85.domain.Task
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TaskActorActions {
  val taskService: TaskService

  def list(userId: Long): Future[Seq[Task]] = {
    taskService.byUser(userId)
  }

  def get(taskId: Long): Future[Option[Task]] = {
    taskService.byId(taskId)
  }

  def create(userId: Long, request: TaskRequest): Future[Task] = {
    taskService.insert(Task(request.name, request.description, userId))
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
