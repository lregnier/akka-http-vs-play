package controllers

import javax.inject._
import com.github.frossi85.database.DB
import play.api.mvc._

@Singleton
class Application @Inject() extends Controller {
  def healthCheck = Action {
    Ok("It's Alive")
  }

  def createSchemas = Action {
    DB.createSchemas()
    Ok("Schemas created")
  }

  def populateDatabase = Action {
    DB.populateWithDummyData()
    Ok("Database populated with dummy data")
  }
}
