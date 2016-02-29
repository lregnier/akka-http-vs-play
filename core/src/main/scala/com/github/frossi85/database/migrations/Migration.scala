package com.github.frossi85.database.migrations

import org.flywaydb.core.Flyway
import slick.migration.api.{SqlMigration, TableMigration}
import slick.migration.flyway.flyway._
import scala.util.{Failure, Success, Try}

abstract class Migration(implicit session : slick.jdbc.JdbcBackend#SessionDef, dialect : slick.migration.api.Dialect[_]) {
  val versionNumber: String = getVersionNumber().toString
  var versionedMigration: VersionedMigration = VersionedMigration(versionNumber, Seq(): _*)

  private def getVersionNumber(): Long = {
    Try(this.getClass.getSimpleName.split("_").last.toLong) match {
      case Success(version) => version
      case Failure(ex) => throw new MigrationTimeStampException(s"Missing or wrong timestamp in class ${this.getClass.getSimpleName}")
    }
  }

  def up(): Unit
  def down(): Unit

  def apply(runner: Flyway): Unit = {
    runner.setResolvers(Resolver(versionedMigration))
    runner.migrate()
  }

  def schema[T <: slick.driver.JdbcDriver#Table[_]](tableQuery: slick.lifted.TableQuery[T], schemaMigration: slick.migration.api.ReversibleTableMigration[T] => TableMigration[T]): Unit = {
    val seedMigration = TableMigration(tableQuery)

    versionedMigration = VersionedMigration(versionNumber, (versionedMigration.migrations ++ List(schemaMigration(seedMigration))): _*)
  }

  def sql(sqlMigration: String): Unit = {
    versionedMigration = VersionedMigration(versionNumber, (versionedMigration.migrations ++ List(SqlMigration(sqlMigration))): _*)
  }

  def sideEffects(actionsToExecute: () => Unit) = {
    val migration = sideEffect { implicit s =>
      actionsToExecute()
    }
    versionedMigration = VersionedMigration(versionNumber, (versionedMigration.migrations ++ List(migration)): _*)
  }
}