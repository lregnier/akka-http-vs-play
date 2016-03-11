package com.github.frossi85.database

import com.github.frossi85.database.migrations._
import slick.jdbc.JdbcBackend


class DatabaseMigrations(migrationsExecutor: MigrationsExecutor)(implicit val db: JdbcBackend#Database, session : slick.jdbc.JdbcBackend#SessionDef, dialect : slick.migration.api.Dialect[_]) {
  def load: MigrationsExecutor = getMigrations.foldLeft(migrationsExecutor)((executor, migration) => executor.add(migration))

  private def getMigrations: List[Migration] = List(
    new CreateTaskTable_20150702112900(),
    new AddSomeTasks_20150802111600()
  )
}
