package com.github.frossi85.database.migrations

import com.github.frossi85.database.tables.{TaskTable, UserTable}
import org.h2.engine.Database
import slick.driver.H2Driver
import slick.lifted.TableQuery


class CreateUserAndTaskTable_20150702112900(implicit val db: H2Driver.api.Database, session : slick.jdbc.JdbcBackend#SessionDef, dialect : slick.migration.api.Dialect[_]) extends Migration {
  val taskQuery = TableQuery[TaskTable]
  val userQuery = TableQuery[UserTable]

  override def up(): Unit = {

    schema[UserTable](userQuery, x => x
      .create
      .addColumns(_.id, _.email, _.password)
    )

    schema[TaskTable](taskQuery, x => x
      .create
      .addColumns(_.id, _.name, _.description, _.userId)
      .addForeignKeys(_.user)
    )

    //sql("insert into testtable (col1, col2) values (1, 2)")
  }

  override def down(): Unit = {
    //Drop things to revert migration goes here

    schema[TaskTable](taskQuery, x => x
      .drop
    )

    schema[UserTable](userQuery, x => x
      .drop
    )
  }
}




