package castalia

import com.twitter.finagle
import com.twitter.finagle.http.{Response, Status, Method, Request}
import com.twitter.util.Await

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

      assert(server.stubsByEndPoint.contains("stub11"))
      assert(server.stubsByEndPoint.contains("jsonconfiguredstub"))

      val request = Request(Method.Get, "/stubs/stub11/1")
      request.host = serverAddress

      Then("the response should be as configured in \"stub1.json\"")
      val response: Response = Await.result(client(request))

      assert(response.status == Status.Ok)
      assert(response.contentString == "{\n  \"id\": \"een\",\n  \"someValue\": \"123123\"\n}")

    }
  }


}
