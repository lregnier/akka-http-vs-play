package com.whiteprompt.services

import com.whiteprompt.domain.{Task, TaskEntity}
import com.whiteprompt.persistence.TaskRepository

import scala.concurrent.{ExecutionContext, Future}

trait TaskService {

  implicit val ec: ExecutionContext
  val taskRepository: TaskRepository

  def create(task: Task): Future[TaskEntity] = {
    for {
      id <- list().map(_.reverse.headOption.map(_.id + 1).getOrElse(1L))
      result <- taskRepository.create(TaskEntity(id, task.name, task.description))
    } yield result
  }

  def retrieve(id: Long): Future[Option[TaskEntity]] = {
    taskRepository.retrieve(id)
  }

  def update(id: Long, toUpdate: Task): Future[Option[TaskEntity]] = {
    taskRepository.update(TaskEntity(id, toUpdate.name, toUpdate.description))
  }

  def delete(id: Long): Future[Option[TaskEntity]] = {
    taskRepository.delete(id)
  }

  def list(): Future[Seq[TaskEntity]] = {
    taskRepository.list()
  }
}


