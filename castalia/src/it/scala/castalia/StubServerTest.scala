package castalia

import com.twitter.finagle
import com.twitter.finagle.http.{Response, Status, Method, Request}
import com.twitter.util.Await
import org.scalatest.DoNotDiscover

@DoNotDiscover
class StubServerTest extends IntegrationTestBase {

  val client = finagle.Http.newService(s"$serverAddress")

  describe("starting up stubserver with 'castaliaT.json'") {

    it("should handle endpoint with one path parameter") {
      When("I do a HTTP GET to '/stubs/stub11/1'")
      val url = "/stub11/1"
      val request = Request(Method.Get, url)
      request.host = serverAddress

      Then("the response should be as configured in 'jsonconfiguredstub_2.json' file")
      val response: Response = Await.result(client(request))

      assert(response.status == Status.Ok)
      assert(response.contentType.get == "application/json")
      assert(response.contentString == """{"id":"een","someValue":"123123"}""")
    }

    it("should responde as configured in 'jsonconfiguredstub_1.json' file") {
      val url = "/doublepathparam/0/responsedata/internalerror"
      When(s"I do a HTTP GET to '$url'")

      val request = Request(Method.Get, url)
      request.host = serverAddress

      Then("the response should be as configured with http status code 503")
      val response: Response = Await.result(client(request))

      assert(response.status == Status.ServiceUnavailable)
      assert(response.contentString == "")
    }

    it("should return 404") {
      val url = "/doesnotexist"
      When(s"I do a HTTP GET to '$url'")

      val request = Request(Method.Get, url)
      request.host = serverAddress

      Then("I should get 404")
      val response: Response = Await.result(client(request))

      assert(response.status == Status.NotFound)
      assert(response.contentString == "Not Found")
    }

    it("should delay at least 100ms") {
      val url = "/doublepathparam/1/responsedata/id1"
      When(s"I do a HTTP GET to '$url'")

      val request = Request(Method.Get, url)
      request.host = serverAddress

      Then("I should get a response after 100 ms")
      val timer = System.currentTimeMillis()
      val response: Response = Await.result(client(request))

      assert(System.currentTimeMillis() - timer > 100)
      assert(response.status == Status.Ok)
      assert(response.contentString == """{"id":"een","someValue":"123123"}""")
    }
  }


}
