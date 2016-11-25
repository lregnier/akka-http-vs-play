import akka.routing.FromConfig
import com.whiteprompt.persistence.TaskRepository
import com.whiteprompt.services.TaskServiceActor
import controllers.{Assets, HealthCheckController, TaskController}
import play.api.ApplicationLoader.Context
import play.api._
import router.Routes

class AppLoader extends ApplicationLoader {
  def load(context: Context) = {
    Logger.configure(context.environment)
    new ApplicationComponents(context).application
  }
}

class ApplicationComponents(context: Context) extends BuiltInComponentsFromContext(context) {
  // Services
  implicit val ec = actorSystem.dispatcher
  val taskService = actorSystem.actorOf(FromConfig.props(TaskServiceActor.props(TaskRepository())), TaskServiceActor.Name)

  // Controllers
  lazy val healthCheckController = new HealthCheckController()
  lazy val taskController = new TaskController(taskService)
  lazy val assets = new Assets(httpErrorHandler)
  override lazy val router = new Routes(httpErrorHandler, healthCheckController, taskController, assets)

}