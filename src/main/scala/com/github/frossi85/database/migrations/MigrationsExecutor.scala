package com.github.frossi85.database.migrations

case class MigrationsExecutor(migrations: Seq[Migration] = Nil) {
  def add(migration: Migration): MigrationsExecutor =
    this.copy(migrations = (migrations ++ List(migration)))

  def runAll = migrations.foreach(x => {
    x.up()
    x.apply()
  })

  def reverseLastOne = {
    val last = migrations.last
    last.down()
    last.apply()
  }

  def revertAll = migrations.reverse.foreach(x => {
    x.down()
    x.apply()
  })
}
