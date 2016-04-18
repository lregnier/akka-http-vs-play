package com.whiteprompt.api.utils

import de.heikoseeberger.akkahttpjson4s._
import org.json4s.{DefaultFormats, jackson}

trait AutoMarshaller extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats
}
