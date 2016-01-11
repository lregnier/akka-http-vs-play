package com.github.frossi85

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.frossi85.database.migrations.{AddTestUserWithSomeTasks_20150802111600, CreateUserAndTaskTable_20150702112900, MigrationsExecutor}
import org.scalatest._
import slick.driver.H2Driver.api._
import slick.migration.api.H2Dialect

abstract class ApiSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterEach with BeforeAndAfterAll {
  val databaseName = java.util.UUID.randomUUID.toString
  val databaseUrl = s"jdbc:h2:mem:$databaseName"

  implicit val db = Database.forURL(databaseUrl, driver="org.h2.Driver")
  implicit val session = db.createSession()
  implicit val dialect = new H2Dialect

  override protected def beforeEach() {
    super.beforeEach()


    /*session.withStatement() { st =>
      st execute "DROP TABLE IF EXISTS \"PUBLIC\".\"schema_version\""
    }*/

    MigrationsExecutor(databaseUrl).add(new CreateUserAndTaskTable_20150702112900()).add(new AddTestUserWithSomeTasks_20150802111600()).runAll()
  }

  override protected def afterEach() {


    MigrationsExecutor(databaseUrl).add(new CreateUserAndTaskTable_20150702112900()).add(new AddTestUserWithSomeTasks_20150802111600()).revertAll()

    /*session.withStatement() { st =>
      st execute "DROP SCHEMA IF EXISTS \"public\""
    }*/


    super.afterEach()
  }
}
