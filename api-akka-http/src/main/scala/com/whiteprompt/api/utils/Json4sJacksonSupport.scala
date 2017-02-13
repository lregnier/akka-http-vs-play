package com.whiteprompt.api.utils

import java.util.UUID

import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.server.PathMatcher.{Matched, Unmatched}
import akka.http.scaladsl.server.PathMatcher1
import de.heikoseeberger.akkahttpjson4s._
import org.json4s.ext.UUIDSerializer
import org.json4s.{DefaultFormats, jackson}

import scala.util.{Failure, Success, Try}

trait Json4sJacksonSupport extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats + UUIDSerializer

  implicit val prettyPrint = Json4sSupport.ShouldWritePretty.True
}

trait CustomDirectives {
  object UUIDSegment extends PathMatcher1[UUID] {
    def apply(path: Path) = path match {
      case Path.Segment(segment, tail) =>
        Try(UUID.fromString(segment)) match {
          case Success(uuid: UUID) => Matched(tail, Tuple1(uuid))
          case Failure(_) => Unmatched
        }
      case _ => Unmatched
    }
  }
}