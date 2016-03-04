package controllers

import akka.actor._
import javax.inject._
import akka.util.Timeout
import com.github.frossi85.database.DB
import com.github.frossi85.domain.Task
import com.github.frossi85.services.{TaskRequest, TaskActor, TaskService}
import play.api.libs.json._
import play.api.mvc._
import slick.jdbc.JdbcBackend
import play.api.data._
import play.api.data.Forms._

import scala.concurrent.Future

@Singleton
class TasksController @Inject() (system: ActorSystem) extends Controller {

  implicit val db: JdbcBackend#Database = DB.db

  import akka.pattern.ask
  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  import scala.concurrent.duration._
  implicit val timeout = Timeout(5 seconds)

  val taskService = new TaskService
  val service = system.actorOf(TaskActor.props(taskService), "my-service-actor")


  val userId = 1L

  implicit val taskImplicitWrites = Json.writes[Task]
  implicit val taskImplicitReads = Json.reads[Task]

  val taskForm = Form(
    mapping(
      "name" -> text,
      "description" -> text
    )(TaskRequest.apply)(TaskRequest.unapply)
  )

  def list = Action.async {
    (service ? TaskActor.GetTasksByUserId(userId))
      .mapTo[Seq[Task]]
      .map(x => Ok(Json.toJson(x)))
  }

  def byId(id: Long) = Action {
    Ok(s"byId $id")
  }

  def create = Action.async { implicit request =>
    taskForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        Future(BadRequest("Something went wrong!!!"))
      },
      taskRequest => {
        (service ? TaskActor.CreateTaskFromRequest(userId, taskRequest))
          .mapTo[Task]
          .map(x => Ok(Json.toJson(x)))
      }
    )
  }

  def update(id: Long) = Action.async { implicit request =>
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
