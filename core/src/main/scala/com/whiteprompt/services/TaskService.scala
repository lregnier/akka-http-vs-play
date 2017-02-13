package com.whiteprompt.services

import java.util.UUID

import com.whiteprompt.domain.{Task, TaskEntity}
import com.whiteprompt.persistence.TaskRepository

import scala.concurrent.{ExecutionContext, Future}

trait TaskService {

  implicit val ec: ExecutionContext
  val taskRepository: TaskRepository

  def create(task: Task): Future[TaskEntity] = {
    val id = UUID.randomUUID()
    taskRepository.create(TaskEntity(id, task.name, task.description))
  }

  def retrieve(id: UUID): Future[Option[TaskEntity]] = {
    taskRepository.retrieve(id)
  }

  def update(id: UUID, toUpdate: Task): Future[Option[TaskEntity]] = {
    taskRepository.update(TaskEntity(id, toUpdate.name, toUpdate.description))
  }

  def delete(id: UUID): Future[Option[TaskEntity]] = {
    taskRepository.delete(id)
  }

  def list(): Future[Seq[TaskEntity]] = {
    taskRepository.list()
  }

}


