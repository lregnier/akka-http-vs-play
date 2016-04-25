package controllers

import javax.inject._
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.whiteprompt.domain.{Task, TaskEntity}
import com.whiteprompt.persistence.TaskRepository
import com.whiteprompt.services.TaskServiceActor
import play.api.data.Forms._
import play.api.data._
import play.api.libs.json._
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.duration._

case class TaskRequest(name: String, description: String) extends Task

@Singleton
class TaskController @Inject()(system: ActorSystem) extends Controller {
  import TaskServiceActor._
  implicit val timeout = Timeout(5 seconds)
  implicit val ec = system.dispatcher

  implicit val taskImplicitWrites = Json.writes[TaskEntity]
  implicit val taskImplicitReads = Json.reads[TaskRequest]

  val taskService = system.actorOf(TaskServiceActor.props(TaskRepository()), "task-service")

  val taskForm = Form(
    mapping(
      "name" -> text,
      "description" -> text
    )(TaskRequest.apply)(TaskRequest.unapply)
  )

  def create = Action.async(BodyParsers.parse.json) { implicit request =>
    taskForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        Future(BadRequest("Something went wrong!!!"))
      },
      taskRequest => {
        (taskService ? CreateTask(taskRequest))
          .mapTo[TaskEntity]
          .map(task => Created("{}")
            .withHeaders(
              LOCATION -> s"${request.uri}/${task.id}"
            )
          )
      }
    )
  }

  def retrieve(id: Long) = Action.async {
    (taskService ? RetrieveTask(id))
      .mapTo[Option[TaskEntity]]
      .map(x  => Ok(Json.toJson(x)))
  }

  def update(id: Long) = Action.async(BodyParsers.parse.json) { implicit request =>
    taskForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        Future(BadRequest("Something went wrong!!!"))
      },
      taskRequest => {
        (taskService ? UpdateTask(id, taskRequest)).mapTo[Option[TaskEntity]].map{
          case Some(task) => Ok(Json.toJson(task))
          case None => NotFound("There is no task with this id")
        }
      }
    )
  }

  def delete(id: Long) = Action.async { implicit request =>
    (taskService ? DeleteTask(id))
      .map(x => NoContent)
  }

  def list = Action.async {
    (taskService ? ListTasks)
      .mapTo[Seq[TaskEntity]]
      .map(x  => Ok(Json.toJson(x)))
  }
}
