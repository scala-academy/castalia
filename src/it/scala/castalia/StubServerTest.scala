package castalia

import castalia.utils.{TestHelpers}
import com.twitter.finagle
import com.twitter.finagle.http.{Method, Request}
import com.twitter.util.Await
import org.scalatest.{Matchers, WordSpec}
import utils.TestHelpers

class StubServerTest extends IntegrationTestBase {
  // TODO: other way than starting application using empty string array in main?

  val client = finagle.Http.newService(s"$serverAddress")
  describe("starting up stubserver") {
    it("should result in a server responding to requests") {
      Given("the server main is running and has an endpoint /status")
      val request = Request(Method.Get, "/status")
      request.host = serverAddress
      When("A request is made to the endpoint")
      val response = client(request)
      Then("The responsecode should be HTTP.OK (200)")
      assert(Await.result(response).statusCode == 200)
    }


    it("should parse the command line arguments") {
      Given("that the stubserver is started with command line argument \"stub1.json jsonconfiguredstub.json\"")

      info("and files \"stub1.json\" and \"jsonconfiguredstub.json\" are both on the classpath")

      When("I do a HTTP GET to the endpoint as configured in \"stub1.json\"")
      val request = Request(Method.Get, "/jsonconfiguredstub")
      request.host = serverAddress


      Then("the response should be as configured in \"stub1.json\"")
      val response = client(request)
      //assert(Await.result(response).statusCode == 200)

      //assert(Await.result(response).toString == "{response stub1}")

      //assert(server.configuration.get === Array("stub1.json", "jsonconfiguredstub.json"))
      assert(server.StubsByEndPoint.head === Array("stub1.json", "jsonconfiguredstub.json"))
//      Map[Endpoint, Map[String, (StatusCode, AnyJsonObject)]]

    }
  }


}
