package com.github.frossi85.services

/*
// Use H2Driver to connect to an H2 database
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import com.github.frossi85.database.tables.{UserTable, TaskTable}
import slick.lifted.TableQuery
import com.localizables.database.DB
import com.localizables.domain.{User, Task}

class TaskService extends DB {
    val tasks = TableQuery[TaskTable]

    def insert(task: Task): Future[Task] = {
        db.run(tasks += task).map(_ => result)
    }

    // will generate sql like:
    //   select * from test where id = ?
    def byId(id: Long): Future[Option[Task]] =  {
        val query = tasks.filter(_.id === id)
        db.run(query.result.headOption)
    }

    def search(filter: GetRequest): Future[Page[Task]] = {
        val location = makePoint(filter.longitude.getOrElse(0.0), filter.latitude.getOrElse(0.0)).asInstanceOf[Rep[Point]]

        val byLocation = filter.hasLocation() match {
            case true => tasks.filter(e => e.location.distanceSphere(location) < filter.distance.bind)
            case false => tasks
        }

        //For now we will filter only for category id and not desendants
        val byCategory = filter.category match {
            case Some(category) => byLocation.filter(e => e.category === category)
            case None => byLocation
        }

        val queryResult = for{
            tasks <- db.run(byCategory.page(filter.page, filter.limit).result)
            total <- db.run(byCategory.length.result)
        } yield (Page(tasks, filter.page, filter.limit, total))

        queryResult
    }
}
*/