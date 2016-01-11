package com.github.frossi85.database.migrations

import org.flywaydb.core.Flyway

case class MigrationsExecutor(databaseUrl: String, user: String = "", password: String = "", migrations: Seq[Migration] = Nil)(implicit session : slick.jdbc.JdbcBackend#SessionDef, dialect : slick.migration.api.Dialect[_]) {

  private def runner: Flyway = {
    val runner = new Flyway()

    runner.setDataSource(databaseUrl, user, password)
    runner.setLocations()
    runner.setValidateOnMigrate(false)
    runner
  }

  def add(migration: Migration): MigrationsExecutor =
    this.copy(migrations = (migrations ++ List(migration)).sortWith(_.versionNumber < _.versionNumber))

  def runAll() = migrations.foreach(x => {
    x.up()
    x.apply(runner)
  })

  def reverseLastOne() = {
    val last = migrations.last
    last.down()
    last.apply(runner)
  }

  def revertAll() = migrations.reverse.foreach(x => {
    x.down()
    x.apply(runner)
  })
}
