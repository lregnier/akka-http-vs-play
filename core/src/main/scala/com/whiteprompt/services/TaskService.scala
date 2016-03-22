package com.whiteprompt.services

import com.whiteprompt.domain.{Task, TaskRequest}
import com.whiteprompt.persistence.TaskRepository

import scala.concurrent.{ExecutionContext, Future}

trait TaskService {

  implicit val executionContext: ExecutionContext
  val taskRepository: TaskRepository

  def create(task: TaskRequest): Future[Task] = {
    for {
      id <- list().map(_.reverse.headOption.map(_.id + 1).getOrElse(1L))
      result <- taskRepository.create(Task(id, task.name, task.description))
    } yield result
  }

  def retrieve(id: Long): Future[Option[Task]] = {
    taskRepository.retrieve(id)
  }

  def update(id: Long, toUpdate: TaskRequest): Future[Option[Task]] = {
    taskRepository.update(Task(id, toUpdate.name, toUpdate.description))
  }

  def delete(id: Long): Future[Option[Task]] = {
    taskRepository.delete(id)
  }

  def list(): Future[Seq[Task]] = {
    taskRepository.list()
  }
}


