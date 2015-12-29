package com.github.frossi85.database.tables

import com.github.frossi85.domain.User
import slick.driver.H2Driver.api._

class UserTable(tag: Tag) extends EntityWithID[User](tag, "Users") {
  def email = column[String]("email")
  def password = column[String]("description")

  def * = (email, password, id) <> (User.tupled, User.unapply)
}
