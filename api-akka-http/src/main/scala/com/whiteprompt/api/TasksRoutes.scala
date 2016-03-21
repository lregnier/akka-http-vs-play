package com.whiteprompt.api

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.whiteprompt.domain.{Task, TaskRequest}
import com.whiteprompt.services.TaskServiceActor
import com.whiteprompt.utils.AutoMarshaller
import akka.pattern.ask

import scala.concurrent.duration._

trait TasksRoutes extends AutoMarshaller {

  import TaskServiceActor._

  implicit val timeout = Timeout(5 seconds)

  val taskService: ActorRef

  def create =
    (pathEnd & post & entity(as[TaskRequest])) { task =>
      onSuccess((taskService ? CreateTask(task)).mapTo[Task]) { task =>
        complete(StatusCodes.Created, task)
      }
    }

  def retrieve =
    (path(LongNumber) & get) { id =>
      onSuccess((taskService ? RetrieveTask(id)).mapTo[Option[Task]]) {
        case Some(task) => complete(task)
        case _ => complete(StatusCodes.NotFound)
      }
    }

  def update =
    (path(LongNumber) & put & entity(as[TaskRequest])) { (id, task)  =>
      onSuccess((taskService ? TaskServiceActor.UpdateTask(id, task)).mapTo[Option[Task]]) {
        case Some(task) => complete(task)
        case _ => complete(StatusCodes.NotFound)
      }
    }

  def remove =
    (path(LongNumber) & delete) { id =>
      onSuccess((taskService ? DeleteTask(id)).mapTo[Option[Task]]) {
        case Some(task) => complete(StatusCodes.NoContent)
        case _ => complete(StatusCodes.NotFound)
      }
    }

  def list =
    (pathEnd & get) {
      onSuccess((taskService ? ListTasks).mapTo[Seq[Task]]) { tasks =>
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

