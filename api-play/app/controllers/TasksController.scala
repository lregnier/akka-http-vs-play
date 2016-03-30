package controllers

import javax.inject._
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.whiteprompt.domain.{TaskEntity}
import com.whiteprompt.services.TaskServiceActor
import play.api.data.Forms._
import play.api.data._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.duration._

@Singleton
class TasksController @Inject() (system: ActorSystem) extends Controller {
  import TaskServiceActor._
  implicit val timeout = Timeout(5 seconds)

  implicit val taskImplicitWrites = Json.writes[TaskEntity]
  implicit val taskImplicitReads = Json.reads[TaskRequest]

  val service = system.actorOf(TaskServiceActor.props(), "task-service")

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
        (service ? CreateTask(taskRequest))
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
    (service ? RetrieveTask(id))
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
        (service ? UpdateTask(id, taskRequest)).mapTo[Option[TaskEntity]].map{
          case Some(task) => Ok(Json.toJson(task))
          case None => NotFound("There is no task with this id")
        }
      }
    )
  }

  def delete(id: Long) = Action.async { implicit request =>
    (service ? DeleteTask(id))
      .map(x => NoContent)
  }

  def list = Action.async {
    (service ? ListTasks)
      .mapTo[Seq[TaskEntity]]
      .map(x  => Ok(Json.toJson(x)))
  }
}
