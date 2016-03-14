package frossi85.database

import com.github.frossi85.database.TestDB
import com.github.frossi85.{ServicesModule, TestDatabaseModule, ConfigModule}
import com.google.inject.{Guice, Injector}
import kamon.Kamon
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

abstract class SpecDB extends FunSuite
  with BeforeAndAfterEach
  with ScalaFutures
  with TestDB
{
  val injector: Injector = Guice.createInjector(
    new ConfigModule(),
    new TestDatabaseModule(),
    new ServicesModule()
  )

  override protected def beforeEach() {
    super.beforeEach()
    Kamon.start()
    initializeDatabase()
  }

  override protected def afterEach() {
    shutdownDatabase()
    Kamon.shutdown()
    super.afterEach()
  }
}





