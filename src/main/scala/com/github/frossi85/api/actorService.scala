
package com.github.frossi85.api

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import com.github.frossi85.services.TaskService
import slick.jdbc.JdbcBackend
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import kamon.trace.Tracer


case class GetCaptureById(id: String)
case class DeleteCaptureById(id: String)
case class GetNotifications(id: String)

class CaptureActor(taskService: TaskService) extends Actor 
	with ActorLogging 
	with CaptureActions {

  override def receive: Receive = {
    case GetCaptureById(id) => {
      Tracer.withNewContext("GetUserDetails", autoFinish = true) {	
      	taskService.byUser(1L) pipeTo sender
	    //getByIdWithDetails(id) pipeTo sender
	  }
    }
  }
}

trait CaptureActions {


  def getByIdWithDetails(id: String): Future[Option[Int]] = {
    Future {
      Some(1)
    }
  }
}
