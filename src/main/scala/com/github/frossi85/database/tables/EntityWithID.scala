package com.github.frossi85.database.tables

// Use H2Driver to connect to an H2 database
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

abstract class EntityWithID[T](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)

    /*def createdAt = column[DateTime]("createdAt", O.NotNull)
    def updatedAt = column[DateTime]("updatedAt", O.NotNull)
    def deletedAt = column[DateTime]("updatedAt", O.NotNull)*/
}