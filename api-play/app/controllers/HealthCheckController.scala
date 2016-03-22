package controllers

import javax.inject._
import play.api.mvc._

@Singleton
`class HealthCheckController @Inject()() extends Controller {
  def healthCheck = Action {
    Ok("It's Alive")
  }
}
