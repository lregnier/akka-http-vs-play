package com.github.frossi85

import slick.jdbc.JdbcBackend
import slick.migration.api.H2Dialect

trait DBTest {
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
