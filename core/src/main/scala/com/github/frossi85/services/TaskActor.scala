package com.github.frossi85.services

import javax.inject.Inject
import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe
import kamon.trace.Tracer

import scala.concurrent.ExecutionContext.Implicits.global

class TaskActor @Inject() (val taskService: TaskServiceInterface) extends Actor
  with ActorLogging
  with TaskActorActions {

  import TaskActor._

  override def receive: Receive = {
    case GetAllTasks() => {
      Tracer.withNewContext("GetTasksByUserId", autoFinish = true) {
      	list pipeTo sender
	    }
    }
    case CreateTaskFromRequest(request) => {
      Tracer.withNewContext("CreateTaskFromRequest", autoFinish = true) {
        create(request) pipeTo sender
      }
    }
    case UpdateTaskFromRequest(taskId, request) => {
      Tracer.withNewContext("UpdateTaskFromRequest", autoFinish = true) {
        update(taskId, request) pipeTo sender
      }
    }
    case GetTaskById(taskId) => {
      Tracer.withNewContext("GetTaskById", autoFinish = true) {
        get(taskId) pipeTo sender
      }
    }
    case DeleteTaskById(taskId) => {
      Tracer.withNewContext("DeleteTaskById", autoFinish = true) {
        taskService.delete(taskId) pipeTo sender
      }
    }
  }
}

object TaskActor {
  def props(taskService: TaskServiceInterface) = Props(classOf[TaskActor], taskService)

  case class UpdateTaskFromRequest(taskId: Long, request: TaskRequest)
  case class DeleteTaskById(taskId: Long)
  case class CreateTaskFromRequest(request: TaskRequest)
  case class GetTaskById(taskId: Long)
  case class GetAllTasks()
}



