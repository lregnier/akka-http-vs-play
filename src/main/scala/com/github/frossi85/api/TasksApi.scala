package com.github.frossi85.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.github.frossi85.database.DB
import com.github.frossi85.domain.Task
import com.github.frossi85.services.TaskService
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

trait TasksApi extends Marshallers {

  Await.result(DB.createSchemas(), Duration.Inf)
  Await.result(DB.populateWithDummyData(), Duration.Inf)


  val taskService = new TaskService

  def byIdRoutes(id: Int) = {
    get {
      complete {
        taskService.byId(id)
      }
    } ~
    (put & entity(as[TaskRequest])) { taskRequest =>
      onComplete(taskService.byId(id).mapTo[Option[Task]]) {
        case Success(v) => v match {
          case Some(task) => complete(taskService.update(task.copy(name = taskRequest.name, description = taskRequest.description)))
          case None => complete(StatusCodes.NotFound)
        }
        case Failure(ex) => complete(StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}")
      }
    } ~
  	delete {
      complete {
        taskService.delete(id).map(x => s"Task with id=$id was deleted")
      }
    }
  }
  
  val tasksRoutes =
    path("tasks") {
      get {
        complete {
          taskService.byUser(1L)
        }
      } ~
      (post & entity(as[TaskRequest])) { taskRequest =>
        complete {
          taskService.insert(Task(taskRequest.name, taskRequest.description, 1L))
        }
      }
    } ~
    path("tasks" / IntNumber) { id => byIdRoutes(id) }
}

