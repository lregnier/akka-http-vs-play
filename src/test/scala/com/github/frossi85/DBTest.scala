package com.github.frossi85

import java.util.logging.{Logger, Level, Handler, LogManager}

import com.github.frossi85.database.migrations.{MigrationsExecutor, CreateUserAndTaskTable_20150702112900, AddTestUserWithSomeTasks_20150802111600}
import com.typesafe.config.ConfigFactory
import slick.jdbc.JdbcBackend
import slick.migration.api.H2Dialect
import com.github.frossi85.database.tables.AgnosticDriver.api._

trait DBTest {
  val conf = ConfigFactory.load()

  val log = LogManager.getLogManager().getLogger("")
  log.getHandlers().foreach(h =>h.setLevel(Level.parse(conf.getString("migrations.logLevel"))))

  val databaseName = java.util.UUID.randomUUID.toString
  val databaseUrl = s"jdbc:h2:mem:$databaseName"

  private implicit val db: JdbcBackend#Database = Database.forURL(databaseUrl, driver="org.h2.Driver")

  implicit val session = db.createSession()
  implicit val dialect = new H2Dialect

  def getDatabase: JdbcBackend#Database = db

  def initializeDatabase() {
    MigrationsExecutor(databaseUrl).add(new CreateUserAndTaskTable_20150702112900()).add(new AddTestUserWithSomeTasks_20150802111600()).runAll()
  }

  def shutdownDatabase() {
    MigrationsExecutor(databaseUrl).add(new CreateUserAndTaskTable_20150702112900()).add(new AddTestUserWithSomeTasks_20150802111600()).revertAll()
  }
}
