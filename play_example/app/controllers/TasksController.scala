package controllers

import akka.actor._
import javax.inject._
import akka.util.Timeout
import com.github.frossi85.database.DB
import com.github.frossi85.domain.Task
import com.github.frossi85.services.{TaskActor, TaskService}
import play.api.libs.json._
import play.api.mvc._
import slick.jdbc.JdbcBackend


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



  def tasks = Action.async {



    (service ? TaskActor.GetTasksByUserId(userId)).mapTo[Seq[Task]].map(x => Ok(Json.toJson(x)))
    //taskService.byUser(userId).map(x => Ok(Json.toJson(x)))
  }

  /*

  def healthCheck = Action {
    Ok("It's Alive")
  }

  def createSchemas = Action {
    DB.createSchemas()
    Ok("Schemas created")
  }

  def populateDatabase = Action {
    DB.populateWithDummyData()
    Ok("Database populated with dummy data")
  }*/
}
