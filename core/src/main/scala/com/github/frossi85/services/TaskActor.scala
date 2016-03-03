package com.github.frossi85.services

import akka.actor.{Props, Actor, ActorLogging}
import akka.pattern.pipe
import kamon.trace.Tracer
import scala.concurrent.ExecutionContext.Implicits.global

class TaskActor(val taskService: TaskService) extends Actor
  with ActorLogging
  with TaskActorActions {

  import TaskActor._

  override def receive: Receive = {
    case GetTasksByUserId(userId) => {
      Tracer.withNewContext("GetTasksByUserId", autoFinish = true) {
      	list(userId) pipeTo sender
	    }
    }
    case CreateTaskFromRequest(userId, request) => {
      Tracer.withNewContext("CreateTaskFromRequest", autoFinish = true) {
        create(userId, request) pipeTo sender
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
  def props(taskService: TaskService) = Props(classOf[TaskActor], taskService)

  case class UpdateTaskFromRequest(taskId: Long, request: TaskRequest)
  case class DeleteTaskById(taskId: Long)
  case class CreateTaskFromRequest(userId: Long, request: TaskRequest)
  case class GetTaskById(taskId: Long)
  case class GetTasksByUserId(userId: Long)
}



