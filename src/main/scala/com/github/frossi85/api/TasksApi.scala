package com.github.frossi85.api

import akka.actor.{Props, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.github.frossi85.domain.Task
import com.github.frossi85.services._
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

  val service = servicesActorSystem.actorOf(Props(classOf[TaskActor], taskService), "my-service-actor")

  val userId = 1L

  def getDatabase: JdbcBackend#Database

  def byIdRoutes(id: Int) =
    get {
      complete {
        (service ? TaskActor.GetTaskById(id)).mapTo[Option[Task]]
      }
    } ~
    (put & entity(as[TaskRequest])) { taskRequest =>
      complete {
        (service ? TaskActor.UpdateTaskFromRequest(id, taskRequest)).mapTo[Option[Task]].map(t => t match {
          case Some(task) => Future(task)
          case None => Future(StatusCodes.NotFound)
        })
      }
    } ~
    delete {
      complete {
        (service ? TaskActor.DeleteTaskById(id)).map(x => s"Task with id=$id was deleted")
      }
    }

  def tasksRoutes =
    path("tasks") {
      get {
        complete {
          Tracer.withNewContext("GetUserDetails-MODDD", autoFinish = true) {  
            (service ? TaskActor.GetTasksByUserId(userId)).mapTo[Seq[Task]]
          }
        }
      } ~
      (post & entity(as[TaskRequest])) { taskRequest =>
        onSuccess((service ? TaskActor.CreateTaskFromRequest(userId, taskRequest)).mapTo[Task]) { task =>
          complete(StatusCodes.Created, task)
        }
      }
    } ~
    path("tasks" / IntNumber) { id => byIdRoutes(id) }
}

