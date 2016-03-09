package com.github.frossi85.services

import com.github.frossi85.database.BaseDao
import com.github.frossi85.database.tables.AgnosticDriver.api._
import com.github.frossi85.database.tables.UserTable
import com.github.frossi85.domain.User
import slick.jdbc.JdbcBackend
import slick.lifted.TableQuery
import scala.concurrent.Future

class UserService(implicit val db: JdbcBackend#Database) extends BaseDao[UserTable, User] {
  override val repository = TableQuery[UserTable]

  override protected def copyWithId(entity: User, id: Long) = entity.copy(id = id)

  def byEmail(email: String): Future[User] = {
    val query = repository.filter(_.email === email)
    db.run(query.result.head)
  }
}

trait UserServiceInterface {
  def byEmail(email: String): Future[User]
}
