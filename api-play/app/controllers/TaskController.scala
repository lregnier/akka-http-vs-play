package controllers

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.whiteprompt.domain.{Task, TaskEntity}
import com.whiteprompt.services.TaskServiceActor
import play.api.data.Forms._
import play.api.data._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

case class TaskData(name: String, description: String) extends Task

class TaskController(val taskService: ActorRef)(implicit val ec: ExecutionContext) extends Controller {
  import TaskServiceActor._
  implicit val timeout = Timeout(5 seconds)

  implicit val taskImplicitReads = Json.reads[TaskData]
  implicit val taskImplicitWrites = Json.writes[TaskEntity]

  val taskForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText
    )(TaskData.apply)(TaskData.unapply)
  )

  def create = Action.async(BodyParsers.parse.json) { implicit request =>
    taskForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest)
      },
      taskData => {
        (taskService ? CreateTask(taskData))
          .mapTo[TaskEntity]
          .map(taskEntity => Created
            .withHeaders(
              LOCATION -> s"${request.uri}/${taskEntity.id}"
            )
          )
      }
    )
  }

  def retrieve(id: String) = Action.async {
    (taskService ? RetrieveTask(id))
      .mapTo[Option[TaskEntity]].map {
        case Some(task) => Ok(Json.toJson(task))
        case None => NotFound
    }
  }

  def update(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    taskForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest)
      },
      taskData => {
        (taskService ? UpdateTask(id, taskData)).mapTo[Option[TaskEntity]].map{
          case Some(taskEntity) => Ok(Json.toJson(taskEntity))
          case None => NotFound
        }
      }
    )
  }

  def delete(id: String) = Action.async {
    (taskService ? DeleteTask(id))
      .mapTo[Option[TaskEntity]].map {
        case Some(taskEntity) => NoContent
        case None => NotFound
    }
  }

  def list = Action.async {
    (taskService ? ListTasks)
      .mapTo[Seq[TaskEntity]]
      .map(tasks => Ok(Json.toJson(tasks)))
  }
}
