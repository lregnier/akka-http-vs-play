package controllers

import play.api.mvc._

class HealthCheckController extends Controller {
  def healthCheck = Action {
    Ok("Play Framework API: up and running!")
  }
}
