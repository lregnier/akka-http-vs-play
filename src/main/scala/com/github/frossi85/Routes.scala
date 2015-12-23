package com.github.frossi85

import akka.http.scaladsl.server.Directives._
import com.github.frossi85.api.TasksApi


import akka.actor.ActorSystem
import akka.http._
import akka.stream._
import akka.http.scaladsl.model._
import spray.json._
import com.typesafe.config.{ ConfigFactory, Config }
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.unmarshalling.{ FromRequestUnmarshaller, Unmarshaller, FromEntityUnmarshaller }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Flow, Sink, Source}
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.math._
import spray.json.DefaultJsonProtocol


trait Routes extends TasksApi {
  val routes = pathPrefix("v1") {
    tasksRoutes
  } ~ 
  path("") {
    get {
      complete {
        <h1>Say hello to akka-http</h1>
      }
    }
  }
}