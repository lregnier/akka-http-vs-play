package com.whiteprompt.api

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Injector
import com.whiteprompt.domain.Task
import com.whiteprompt.services.{TaskActor, TaskRequest, TaskServiceInterface}
import kamon.trace.Tracer
import net.codingwell.scalaguice.InjectorExtensions._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

trait TasksApi extends AutoMarshaller {
  implicit val timeout = Timeout(5 seconds)

  val injector: Injector

  lazy val service = {
    val taskService: TaskServiceInterface = injector.instance[TaskServiceInterface]
    val serviceSystem: ActorSystem = ActorSystem("tasks-actor")
    serviceSystem.actorOf(Props(classOf[TaskActor], taskService), "my-service-actor")
  }

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
            (service ? TaskActor.GetAllTasks()).mapTo[Seq[Task]]
          }
        }
      } ~
      (post & entity(as[TaskRequest])) { taskRequest =>
        onSuccess((service ? TaskActor.CreateTaskFromRequest(taskRequest)).mapTo[Task]) { task =>
          complete(StatusCodes.Created, task)
        }
      }
    } ~
    path("tasks" / IntNumber) { id => byIdRoutes(id) }
}

