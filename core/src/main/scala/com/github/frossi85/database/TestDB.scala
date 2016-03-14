package com.github.frossi85.database

import java.util.logging.{Level, LogManager}
import com.github.frossi85.database.migrations.MigrationsExecutor
import com.google.inject.Injector
import com.typesafe.config.ConfigFactory
import net.codingwell.scalaguice.InjectorExtensions._
import slick.jdbc.JdbcBackend
import slick.migration.api.H2Dialect

trait TestDB {
  val injector: Injector

  implicit lazy val db: JdbcBackend#Database = injector.instance[JdbcBackend#Database]

  val conf = ConfigFactory.load()

  val log = LogManager.getLogManager().getLogger("")
  log.getHandlers().foreach(h =>h.setLevel(Level.parse(conf.getString("migrations.logLevel"))))

  implicit val session = db.createSession()
  implicit val dialect = new H2Dialect

  val migrationsExecutor = new DatabaseMigrations(MigrationsExecutor(session.metaData.getURL)).load

  def initializeDatabase() {
    migrationsExecutor.runAll()
  }

  def shutdownDatabase() {
    migrationsExecutor.revertAll()
  }
}


