package castalia

import com.twitter.finagle
import com.twitter.finagle.http.{Response, Status, Method, Request}
import com.twitter.util.Await
import org.scalatest.DoNotDiscover

@DoNotDiscover
class StubServerTest extends IntegrationTestBase {

  val client = finagle.Http.newService(s"$serverAddress")

  describe("starting up stubserver with 'castaliaT.json'") {

    it("should responde as configured in 'jsonconfiguredstub_2.json' file") {
      When("I do a HTTP GET to '/stubs/stub11/1'")
      val url = "/stubs/stub11/1"
      val request = Request(Method.Get, url)
      request.host = serverAddress

      Then("the response should be as configured with http status code 200")
      val response: Response = Await.result(client(request))

      assert(response.status == Status.Ok)
      assert(response.contentString == "{\n  \"id\": \"een\",\n  \"someValue\": \"123123\"\n}")
    }

    it("should responde as configured in 'jsonconfiguredstub_1.json' file") {
      val url = "/stubs/doublepathparam/0/responsedata/internalerror"
      When(s"I do a HTTP GET to '$url'")

      val request = Request(Method.Get, url)
      request.host = serverAddress

      Then("the response should be as configured with http status code 503")
      val response: Response = Await.result(client(request))

      assert(response.status == Status.ServiceUnavailable)
      assert(response.contentString == "")
    }

    it("should return 404") {
      val url = "/stubs/doesnotexist"
      When(s"I do a HTTP GET to '$url'")

      val request = Request(Method.Get, url)
      request.host = serverAddress

      Then("I should get 404")
      val response: Response = Await.result(client(request))

      assert(response.status == Status.NotFound)
      assert(response.contentString == "Not Found")
    }
  }


}
