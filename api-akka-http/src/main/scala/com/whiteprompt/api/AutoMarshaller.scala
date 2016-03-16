package com.whiteprompt.api

import de.heikoseeberger.akkahttpjson4s._
import org.json4s.{DefaultFormats, jackson}

trait AutoMarshaller extends Json4sSupport {
  implicit val serialization = jackson.Serialization // or native.Serialization
  implicit val formats = DefaultFormats
}
