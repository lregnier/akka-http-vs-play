package com.github.frossi85.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.github.frossi85.domain.{User, Task}
import spray.json.DefaultJsonProtocol

trait Marshallers extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val TaskFormat = jsonFormat4(Task)

  implicit val UserFormat = jsonFormat3(User)

  implicit val TaskRequestFormat = jsonFormat2(TaskRequest)
}