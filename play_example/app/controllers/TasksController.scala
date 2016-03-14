package controllers

import javax.inject._

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.github.frossi85.domain.Task
import com.github.frossi85.services.{TaskServiceInterface, TaskActor, TaskRequest, TaskService}
import play.api.data.Forms._
import play.api.data._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import slick.jdbc.JdbcBackend

import scala.concurrent.Future
import scala.concurrent.duration._

@Singleton
class TasksController @Inject() (system: ActorSystem, taskService: TaskServiceInterface) extends Controller {
  implicit val timeout = Timeout(5 seconds)

  val service = system.actorOf(TaskActor.props(taskService), "my-service-actor")

  implicit val taskImplicitWrites = Json.writes[Task]
  implicit val taskImplicitReads = Json.reads[TaskRequest]

  val taskForm = Form(
    mapping(
      "name" -> text,
      "description" -> text
    )(TaskRequest.apply)(TaskRequest.unapply)
  )

  def list = Action.async {
    (service ? TaskActor.GetAllTasks())
      .mapTo[Seq[Task]]
      .map(x => Ok(Json.toJson(x)))
  }

  def byId(id: Long) = Action.async {
    (service ? TaskActor.GetTaskById(id))
      .mapTo[Option[Task]]
      .map(x  => Ok(Json.toJson(x)))
  }

  def create = Action.async(BodyParsers.parse.json) { implicit request =>
    taskForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        Future(BadRequest("Something went wrong!!!"))
      },
      taskRequest => {
        (service ? TaskActor.CreateTaskFromRequest(taskRequest))
          .mapTo[Task]
          .map(x => Created(Json.toJson(x)))
      }
    )
  }

  def update(id: Long) = Action.async(BodyParsers.parse.json) { implicit request =>
    taskForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        Future(BadRequest("Something went wrong!!!"))
      },
      taskRequest => {
        (service ? TaskActor.UpdateTaskFromRequest(id, taskRequest)).mapTo[Option[Task]].map(t => t match {
          case Some(task) => Ok(Json.toJson(task))
          case None => NotFound("There is no task with this id")
        })
      }
    )
  }

  def delete(id: Long) = Action.async { implicit request =>
      (service ? TaskActor.DeleteTaskById(id))
        .map(x => Ok(s"Task with id=$id was deleted"))
  }
}
