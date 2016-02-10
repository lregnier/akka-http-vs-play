package com.github.frossi85.api

import akka.actor.{Props, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.github.frossi85.domain.Task
import com.github.frossi85.services.TaskService
import kamon.trace.Tracer
import slick.jdbc.JdbcBackend
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.ask

trait TasksApi extends AutoMarshaller {
  implicit val db: JdbcBackend#Database = getDatabase

  implicit val timeout = Timeout(5 seconds)

  val servicesActorSystem = ActorSystem("FACU-actor-system")

  val taskService = new TaskService

  val service = servicesActorSystem.actorOf(Props(classOf[CaptureActor], taskService), "my-service-actor")

  def getDatabase: JdbcBackend#Database

  def byIdRoutes(id: Int) =
    get {
      complete {
        taskService.byId(id)
      }
    } ~
    (put & entity(as[TaskRequest])) { taskRequest =>
      complete {
        taskService.byId(id).map(t => t match {
          case Some(task) => taskService.update(task.copy(name = taskRequest.name, description = taskRequest.description))
          case None => Future(StatusCodes.NotFound)
        })
      }
    } ~
    delete {
      complete {
        taskService.delete(id).map(x => s"Task with id=$id was deleted")
      }
    }

  def tasksRoutes =
    path("tasks") {
      get {
        complete {

          Tracer.withNewContext("GetUserDetails-MODDD", autoFinish = true) {  
            (service ? GetCaptureById("Hello")).mapTo[Seq[Task]] 
          }
          //taskService.byUser(1L)
        }
      } ~
      (post & entity(as[TaskRequest])) { taskRequest =>
        onSuccess(taskService.insert(Task(taskRequest.name, taskRequest.description, 1L))) { task =>
          complete(StatusCodes.Created, task)
        }
      }
    } ~
    path("tasks" / IntNumber) { id => byIdRoutes(id) }
}

