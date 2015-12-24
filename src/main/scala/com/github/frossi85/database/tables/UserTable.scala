package com.github.frossi85.database.tables

// Use H2Driver to connect to an H2 database
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import com.github.frossi85.domain.User

class UserTable(tag: Tag) extends EntityWithID[User](tag, "Users") {
  def email = column[String]("email")
  def password = column[String]("description")

  def * = (email, password, id) <> (User.tupled, User.unapply)
}
