package com.github.frossi85

import java.util.logging.{Level, LogManager}
import javax.inject.{Inject, Provider, Singleton}

import com.github.frossi85.database.DatabaseMigrations
import com.github.frossi85.database.migrations.MigrationsExecutor
import com.google.inject.AbstractModule
import com.typesafe.config.Config
import slick.jdbc.JdbcBackend
import slick.migration.api.H2Dialect

class DatabaseModule()
  extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[slick.jdbc.JdbcBackend.Database]).toProvider(classOf[DatabaseProvider])
  }
}

class TestDatabaseModule()
  extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[slick.jdbc.JdbcBackend.Database]).toProvider(classOf[TestDatabaseProvider])
  }
}

@Singleton
class DatabaseProvider @Inject() (config: Config) extends Provider[slick.jdbc.JdbcBackend.Database] {
  private val db = slick.jdbc.JdbcBackend.Database.forConfig("h2mem1", config)

  override def get(): JdbcBackend.DatabaseDef = db
}

@Singleton
class TestDatabaseProvider @Inject() (config: Config) extends Provider[slick.jdbc.JdbcBackend.Database] {
  val log = LogManager.getLogManager().getLogger("")
  log.getHandlers().foreach(h =>h.setLevel(Level.parse(config.getString("migrations.logLevel"))))

  val databaseName = java.util.UUID.randomUUID.toString
  val databaseUrl = s"jdbc:h2:mem:$databaseName"

  private implicit val db: JdbcBackend#Database = slick.jdbc.JdbcBackend.Database.forURL(databaseUrl, driver="org.h2.Driver")//slick.jdbc.JdbcBackend.Database.forConfig("h2mem1", config)

  implicit val session = db.createSession()
  implicit val dialect = new H2Dialect

  val migrationsExecutor = new DatabaseMigrations(MigrationsExecutor(databaseUrl)).load

  migrationsExecutor.runAll()

  override def get(): JdbcBackend.DatabaseDef = db.asInstanceOf[JdbcBackend.DatabaseDef]
}