package com.github.frossi85.services

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe
import com.github.frossi85.guice.NamedActor
import javax.inject.{Named, Singleton, Inject}
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

object TaskActor extends NamedActor {
  // this (and the NamedActor trait) is not required here -- it is simply a convenience so that the name
  // can be defined and referenced from one place
  override final val name = "TaskActor"

  def props(taskService: TaskServiceInterface) = Props(classOf[TaskActor], taskService)

  case class UpdateTaskFromRequest(taskId: Long, request: TaskRequest)
  case class DeleteTaskById(taskId: Long)
  case class CreateTaskFromRequest(request: TaskRequest)
  case class GetTaskById(taskId: Long)
  case class GetAllTasks()
}



