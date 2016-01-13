package com.github.frossi85.database.tables

class UserTable(tag: Tag) extends EntityWithID[User](tag, "Users") {
  def email = column[String]("email")
  def password = column[String]("description")

  def * = (email, password, id) <> (User.tupled, User.unapply)
}
