package com.github.frossi85.database

import java.util.logging.{Level, LogManager}
import com.github.frossi85.database.migrations.MigrationsExecutor
import com.github.frossi85.database.tables.AgnosticDriver.api._
import com.typesafe.config.ConfigFactory
import slick.jdbc.JdbcBackend
import slick.migration.api.H2Dialect

/**
  * Created by facundo on 01/03/16.
  */
trait TestDB {
  val conf = ConfigFactory.load()

  val log = LogManager.getLogManager().getLogger("")
  log.getHandlers().foreach(h =>h.setLevel(Level.parse(conf.getString("migrations.logLevel"))))

  val databaseName = java.util.UUID.randomUUID.toString
  val databaseUrl = s"jdbc:h2:mem:$databaseName"

  private implicit val db: JdbcBackend#Database = Database.forURL(databaseUrl, driver="org.h2.Driver")

  implicit val session = db.createSession()
  implicit val dialect = new H2Dialect

  val migrationsExecutor = new DatabaseMigrations(MigrationsExecutor(databaseUrl)).load

  def getDatabase: JdbcBackend#Database = db

  def initializeDatabase() {
    migrationsExecutor.runAll()
  }

  def shutdownDatabase() {
    migrationsExecutor.revertAll()
  }
}


