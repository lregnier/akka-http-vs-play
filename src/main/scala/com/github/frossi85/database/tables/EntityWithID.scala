package com.github.frossi85.database.tables

import slick.driver.H2Driver.api._

abstract class EntityWithID[T](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)

    /*def createdAt = column[DateTime]("createdAt", O.NotNull)
    def updatedAt = column[DateTime]("updatedAt", O.NotNull)
    def deletedAt = column[DateTime]("updatedAt", O.NotNull)*/
}