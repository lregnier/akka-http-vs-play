package com.github.frossi85.database.migrations

import com.github.frossi85.database.DB
import org.flywaydb.core.Flyway
import slick.migration.api.{SqlMigration, TableMigration, H2Dialect}
import slick.migration.flyway.flyway._

import scala.util.{Failure, Success, Try}

abstract class Migration {
  val versionNumber: Int = getVersionNumber()
  var versionedMigration: VersionedMigration = VersionedMigration(versionNumber, Seq(): _*)

  implicit val session = DB.db.createSession()

  implicit val dialect = new H2Dialect

  private def getVersionNumber(): Int = {
    Try(this.getClass.getSimpleName.split("_").last.toInt) match {
      case Success(version) => version
      case Failure(ex) => throw new MigrationTimeStampException(s"Missing or wrong timestamp in class ${this.getClass.getSimpleName}")
    }
  }

  def up(): Unit
  def down(): Unit

  def apply(): Unit = {
    val flyway = new Flyway()
    flyway.setDataSource("jdbc:h2:mem:$databasename", "", "")
    flyway.setLocations()

    flyway.setResolvers(Resolver(versionedMigration))

    flyway.migrate()
  }

  def schema[T <: slick.driver.JdbcDriver#Table[_]](tableQuery: slick.lifted.TableQuery[T], schemaMigration: slick.migration.api.ReversibleTableMigration[T] => TableMigration[T]): Unit = {
    val seedMigration = TableMigration(tableQuery)

    versionedMigration = VersionedMigration(versionedMigration.version, (versionedMigration.migrations ++ List(schemaMigration(seedMigration))): _*)
  }

  def sql(sqlMigration: String): Unit = {
    versionedMigration = VersionedMigration(versionedMigration.version, (versionedMigration.migrations ++ List(SqlMigration(sqlMigration))): _*)
  }

  def sideEffects(actionsToExecute: () => Unit) = {
    val migration = sideEffect { implicit s =>
      actionsToExecute()
    }
    versionedMigration = VersionedMigration(versionedMigration.version, (versionedMigration.migrations ++ List(migration)): _*)
  }
}