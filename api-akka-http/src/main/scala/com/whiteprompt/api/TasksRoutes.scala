package com.whiteprompt.api

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Injector
import com.whiteprompt.domain.Task
import com.whiteprompt.services.{TaskServiceActor$, TaskRequest, TaskServiceInterface}
import net.codingwell.scalaguice.InjectorExtensions._

import scala.concurrent.duration._

trait TasksRoutes extends AutoMarshaller {
  implicit val timeout = Timeout(5 seconds)

  val injector: Injector

  lazy val service = {
    val taskService: TaskServiceInterface = injector.instance[TaskServiceInterface]
    val serviceSystem: ActorSystem = ActorSystem("tasks-actor")
    serviceSystem.actorOf(Props(classOf[TaskServiceActor], taskService), "my-service-actor")
  }

  def create =
    (pathEnd & post & entity(as[TaskRequest])) { task =>
      onSuccess((service ? TaskServiceActor.CreateTaskFromRequest(task)).mapTo[Task]) { task =>
        complete(StatusCodes.Created, task)
      }
    }

  def retrieve =
    (path(LongNumber) & get) { id =>
      onSuccess((service ? TaskServiceActor.GetTaskById(id)).mapTo[Option[Task]]) {
        case Some(task) => complete(task)
        case _ => complete(StatusCodes.NotFound)
      }
    }

  def update =
    (path(LongNumber) & put & entity(as[TaskRequest])) { (id, task)  =>
      onSuccess((service ? TaskServiceActor.UpdateTaskFromRequest(id, task)).mapTo[Option[Task]]) {
        case Some(task) => complete(task)
        case _ => complete(StatusCodes.NotFound)
      }
    }

  def delete =
    (path(LongNumber) & delete) { id =>
      onSuccess((service ? TaskServiceActor.DeleteTaskById(id)) {
        case Some(id) => complete(StatusCodes.NoContent)
        case _ => complete(StatusCodes.NotFound)
      }
    }

  def list =
    (pathEnd & get) {
      onSuccess((service ? TaskServiceActor.GetAllTasks()).mapTo[Seq[Task]]) { tasks =>
        complete(tasks)
      }
    }

  def tasksRoutes =
    pathPrefix("tasks") {
      create ~
      retrieve ~
      update ~
      delete ~
      list
    }
}

