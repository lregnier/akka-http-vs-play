package com.github.frossi85.stress_tests

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://toolbarqueries.google.com")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png"""), WhiteList())

	val headers_0 = Map("X-Client-Data" -> "CKW2yQEI/ZXKAQ==")

	val headers_1 = Map("Pragma" -> "no-cache")

    val uri1 = "http://www.gstatic.com/generate_204"
    val uri2 = "http://toolbarqueries.google.com/tbr"

	val scn = scenario("RecordedSimulation")
		.exec(http("request_0")
			.get("/tbr?client=navclient-auto&features=Rank&ch=64060687804&q=info:http://localhost:8080/create-schemas")
			.headers(headers_0)
			.check(status.is(403)))
		.pause(59)
		.exec(http("request_1")
			.get(uri1 + "")
			.headers(headers_1))
		.pause(100)
		.exec(http("request_2")
			.get("/tbr?client=navclient-auto&features=Rank&ch=6914815985&q=info:http://localhost:8080/v1/tasks")
			.headers(headers_0)
			.check(status.is(403)))
		.pause(34)
		.exec(http("request_3")
			.get(uri1 + "")
			.headers(headers_1))
		.pause(17)
		.exec(http("request_4")
			.get("/tbr?client=navclient-auto&features=Rank&ch=61179348087&q=info:http://localhost:8080/v1/tasks/1")
			.headers(headers_0)
			.check(status.is(403)))
		.pause(2)
		.exec(http("request_5")
			.get("/tbr?client=navclient-auto&features=Rank&ch=63940724394&q=info:http://localhost:8080/v1/tasks/2")
			.headers(headers_0)
			.check(status.is(403)))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}