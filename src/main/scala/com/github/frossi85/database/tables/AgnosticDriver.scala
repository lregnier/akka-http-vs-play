package com.github.frossi85.database.tables

import com.typesafe.config.ConfigFactory
import slick.driver.{H2Driver, JdbcDriver, MySQLDriver, PostgresDriver}

object AgnosticDriver {
  val api = profile.api //.simple
  lazy val profile: JdbcDriver = {
     ConfigFactory.load().getString("databaseDriver") match {
        case "slick.driver.MySQLDriver" => MySQLDriver
        case "slick.driver.PostgresDriver" => PostgresDriver
        case _ => H2Driver
      }
  }
}
