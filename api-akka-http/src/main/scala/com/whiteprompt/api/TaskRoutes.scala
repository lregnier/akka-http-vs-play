package com.whiteprompt.api

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import com.whiteprompt.api.utils.AutoMarshaller
import com.whiteprompt.domain.{Task, TaskEntity}
import com.whiteprompt.services.TaskServiceActor

import scala.concurrent.duration._

case class TaskData(name: String, description: String) extends Task {
  require(!name.isEmpty)
  require(!description.isEmpty)
}

trait TaskRoutes extends AutoMarshaller {
  import TaskServiceActor._

  implicit val timeout = Timeout(5 seconds)

  val taskService: ActorRef

  def create =
    (pathEnd & post & entity(as[TaskData])) { task =>
      extractUri { uri =>
        onSuccess((taskService ? CreateTask(task)).mapTo[TaskEntity]) { task =>
          respondWithHeader(Location(s"$uri/${task.id}")) {
            complete(StatusCodes.Created)
          }
        }
      }
    }

  def retrieve =
    (path(LongNumber) & get) { id =>
      onSuccess((taskService ? RetrieveTask(id)).mapTo[Option[TaskEntity]]) {
        case Some(task) => complete(task)
        case None => complete(StatusCodes.NotFound)
      }
    }

  def update =
    (path(LongNumber) & put & entity(as[TaskData])) { (id, task)  =>
      onSuccess((taskService ? TaskServiceActor.UpdateTask(id, task)).mapTo[Option[TaskEntity]]) {
        case Some(task) => complete(task)
        case None => complete(StatusCodes.NotFound)
      }
    }

  def remove =
    (path(LongNumber) & delete) { id =>
      onSuccess((taskService ? DeleteTask(id)).mapTo[Option[TaskEntity]]) {
        case Some(task) => complete(StatusCodes.NoContent)
        case None => complete(StatusCodes.NotFound)
      }
    }

  def list =
    (pathEnd & get) {
      onSuccess((taskService ? ListTasks).mapTo[Seq[TaskEntity]]) { tasks =>
        complete(tasks)
      }
    }

  def tasksRoutes =
    pathPrefix("tasks") {
      create ~
      retrieve ~
      update ~
      remove ~
      list
    }
}

