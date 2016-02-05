/*
package com.github.frossi85.api

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


case class GetCaptureById(id: String)
case class DeleteCaptureById(id: String)
case class GetNotifications(id: String)

class CaptureActor extends Actor with ActorLogging with CaptureActions {
  val
  override def receive: Receive = {
    case GetCaptureById(id) => getByIdWithDetails(id) pipeTo sender
  }
}

trait CaptureActions {


  def getByIdWithDetails(id: String): Future[Option[Int]] = {
    Future {
      Some(1)
    }
  }
}*/
