package modules

import com.google.inject.AbstractModule
import com.whiteprompt.database.Repository
import com.whiteprompt.domain.Task
import com.whiteprompt.services.{TaskServiceInterface, TaskService}
import net.codingwell.scalaguice.ScalaModule

class ServicesModule extends AbstractModule with ScalaModule {
  override def configure() {
    bind[Repository[Task]].to[TaskService].asEagerSingleton()
    bind[TaskServiceInterface].to[TaskService].asEagerSingleton()
  }
}