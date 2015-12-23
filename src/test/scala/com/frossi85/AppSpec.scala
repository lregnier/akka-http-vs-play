class AppSpec extends FlatSpec with Matchers with ScalaFutures with BeforeAndAfterAll {
 
  implicit val testSystem = akka.actor.ActorSystem("test-system")
  
  import testSystem.dispatcher
  implicit val fm = ActorFlowMaterializer()
  
  val server = new SampleApp {}
 
  override def afterAll = testSystem.shutdown()
 
  def sendRequest(req: HttpRequest) =
    Source.single(req).via(
      Http().outgoingConnection(
        host = "localhost",
        port = 8080
      ).flow
    ).runWith(Sink.head)
 
  "The app" should "return index.html on a GET to /" in {
    val request = sendRequest(HttpRequest())
    whenReady(request) { response =>
      val stringFuture = Unmarshal(response.entity).to[String]
      whenReady(stringFuture) { str =>
        str should include("Hello World!")
      }
    }
  }
  "The app" should "return 404 on a GET to /foo" in {
    val request = sendRequest(HttpRequest(uri = "/foo"))
    whenReady(request) { response =>
      response.status shouldBe StatusCodes.NotFound
    }
  }
}