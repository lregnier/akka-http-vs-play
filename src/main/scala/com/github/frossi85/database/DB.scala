package com.github.frossi85.database

// Use H2Driver to connect to an H2 database
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import com.github.frossi85.database.tables.{UserTable, TaskTable}
import com.github.frossi85.domain.{User, Task}
import slick.lifted.TableQuery

trait DB {
    val db = Database.forConfig("h2mem1")
    val users = TableQuery[UserTable]
    val tasks = TableQuery[TaskTable]

    def setUp() = {
        db.run(DBIO.seq((
                users.schema ++
                tasks.schema
            ).create
        ))
    }

    def populateWithDummyData = {
      val setup = DBIO.seq(
        // Insert some suppliers
        users += User("frossi85@gmail.com", "11111111"),

        // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
        tasks ++= Seq(
          Task("Task 1", "One description", 1L),
          Task("Task 2", "Another description", 1L)
        )
      )
      db.run(setup)
    }
/*

    db.run(tasks.result).map(_.foreach {
      case (name, supID, price, sales, total) =>
        println("  " + name + "\t" + supID + "\t" + price + "\t" + sales + "\t" + total)
    })


    def byId(id: Long) = {
      val q2 = for {
        c <- coffees if c.price < 9.0
        s <- suppliers if s.id === c.supID
      } yield (c.name, s.name)
    }
    */
}