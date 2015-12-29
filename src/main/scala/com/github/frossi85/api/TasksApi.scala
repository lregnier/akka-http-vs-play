package com.github.frossi85.api

import akka.http.scaladsl.server.Directives._
import com.github.frossi85.services.TaskService


trait TasksApi {

  val taskService = new TaskService

  def byIdRoutes(id: Int) = {
	get {
	  complete {
	    "Received GET request for task " + id
	  }
	} ~
  	put {
      complete {
        "Received PUT request for task " + id
      }
    } ~
  	delete {
      complete {
        "Received DELETE request for task " + id
      }
    }
  }
  
  val tasksRoutes =
    path("tasks") {
      get {
        complete {
          "Received GET request for tasks"
        }
      } ~
      post {
        complete {
          "Received POST request for tasks"
        }
      }
    } ~
    path("tasks" / IntNumber) { id => byIdRoutes(id) }
}
