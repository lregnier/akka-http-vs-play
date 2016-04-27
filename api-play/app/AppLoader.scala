import com.whiteprompt.common.KamonHandler
import com.whiteprompt.persistence.TaskRepository
import com.whiteprompt.services.TaskServiceActor
import controllers.{Assets, HealthCheckController, TaskController}
import play.api.ApplicationLoader.Context
import play.api._
import router.Routes

import scala.concurrent.Future

class AppLoader extends ApplicationLoader {
  def load(context: Context) = {
    Logger.configure(context.environment)
    new ApplicationComponents(context).application
  }
}

class ApplicationComponents(context: Context) extends BuiltInComponentsFromContext(context) with KamonHandler {

  implicit val ec = actorSystem.dispatcher
  val taskService = actorSystem.actorOf(TaskServiceActor.props(TaskRepository()), "task-service")

  lazy val healthCheckController = new HealthCheckController()
  lazy val taskController = new TaskController(taskService)
  lazy val assets = new Assets(httpErrorHandler)
  override lazy val router = new Routes(httpErrorHandler, healthCheckController, taskController, assets)

  // Adding hook for stopping Kamon
  applicationLifecycle.addStopHook(() => Future.successful(stopKamon()))
}