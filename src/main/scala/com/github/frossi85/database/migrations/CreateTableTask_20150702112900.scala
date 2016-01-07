package com.github.frossi85.database.migrations

import com.github.frossi85.database.DB
import com.github.frossi85.database.tables.TaskTable
import org.flywaydb.core.Flyway
import slick.lifted.TableQuery
import slick.migration.api.{H2Dialect, SqlMigration, TableMigration}
import slick.migration.flyway.flyway.{Resolver, VersionedMigration, sideEffect}
import slick.model.Table
import slick.profile.RelationalProfile
import slick.driver.H2Driver.api._


class CreateTableTask_20150702112900 extends Migration {
  override def up(): Unit = {
    val tableQuery = TableQuery[TaskTable]

    schema[TaskTable](tableQuery, x => x
      .create
      .addColumns(_.name, _.description, _.userId)
      .addForeignKeys(_.user)
    )

    sql("insert into testtable (col1, col2) values (1, 2)")

    sideEffects(() => {
      //Slick queries like insert goes here
    })
  }

  override def down(): Unit = {
    //Drop things to revert migration goes here
  }
}




