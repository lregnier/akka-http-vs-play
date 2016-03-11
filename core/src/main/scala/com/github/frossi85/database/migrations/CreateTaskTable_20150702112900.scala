package com.github.frossi85.database.migrations

import com.github.frossi85.database.tables.TaskTable
import slick.jdbc.JdbcBackend
import slick.lifted.TableQuery

class CreateTaskTable_20150702112900(implicit val db: JdbcBackend#Database, session : slick.jdbc.JdbcBackend#SessionDef, dialect : slick.migration.api.Dialect[_]) extends Migration {
  val taskQuery = TableQuery[TaskTable]

  override def up(): Unit = {
    schema[TaskTable](taskQuery, x => x
      .create
      .addColumns(_.id, _.name, _.description)
    )

    //sql("insert into testtable (col1, col2) values (1, 2)")
  }

  override def down(): Unit = {
    //Drop things to revert migration goes here

    schema[TaskTable](taskQuery, x => x
      .drop
    )
  }
}




