package castalia

import com.twitter.finagle
import com.twitter.finagle.http.{Response, Status, Method, Request}
import com.twitter.util.Await
import org.scalatest.DoNotDiscover
import scala.util.Random

@DoNotDiscover
class StubServerTest extends IntegrationTestBase {

  val clientServer = finagle.Http.newService(s"$serverAddress")
  val clientManager = finagle.Http.newService(s"$managerAddress")

  describe("Stubserver started with 'castaliaT.json'") {

    it("should handle endpoint with one path parameter") {
      When("I do a HTTP GET to '/stubs/stub11/1'")
      val url = "/stub11/1"
      val request = Request(Method.Get, url)
      request.host = serverAddress

      Then("the response should be as configured in 'jsonconfiguredstub_2.json' file")
      val response: Response = Await.result(clientServer(request))

      assert(response.status == Status.Ok)
      assert(response.contentType.get == "application/json")
      assert(response.contentString == """{"id":"een","someValue":"123123"}""")
    }

    it("should respond as configured in 'jsonconfiguredstub_1.json' file") {
      val url = "/doublepathparam/0/responsedata/internalerror"
      When(s"I do a HTTP GET to '$url'")

      val request = Request(Method.Get, url)
      request.host = serverAddress

      Then("the response should be as configured with http status code 503")
      val response: Response = Await.result(clientServer(request))

      assert(response.status == Status.ServiceUnavailable)
      assert(response.contentString == "")
    }

    it("should return 404") {
      val url = "/doesnotexist"
      When(s"I do a HTTP GET to '$url'")

      val request = Request(Method.Get, url)
      request.host = serverAddress

      Then("I should get 404")
      val response: Response = Await.result(clientServer(request))

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
      val response: Response = Await.result(clientServer(request))

      assert(System.currentTimeMillis() - timer > 100)
      assert(response.status == Status.Ok)
      assert(response.contentString == """{"id":"een","someValue":"123123"}""")
    }

    it("should allow adding new response to endpoint during runtime"){
      val stubUrl = "doublepathparam/$1/responsedata/$2"
      val manageResponsesUrl = "/castalia/manager/endpoints/responses"
      val newParametersUrl = "/doublepathparam/2/responsedata/id2"

      Given(s"the stubserver is running with a stub defined at endpoint '$stubUrl'")

      val requestBefore = Request(Method.Get, newParametersUrl)
      requestBefore.host = serverAddress
      val responseBefore: Response = Await.result(clientServer(requestBefore))

      assert(responseBefore.status == Status.Forbidden)
      assert(responseBefore.contentString == "Forbidden")

      When(s"I POST a new response for this endpoint to $manageResponsesUrl")

      val postRequest = Request(Method.Post, manageResponsesUrl)
      postRequest.host = managerAddress
      postRequest.contentType = "application/json"
      postRequest.contentString = "{ \"endpoint\": \"doublepathparam/$1/responsedata/$2\", " +
        "\"response\": {" +
          "\"ids\": {\"1\": \"2\",\"2\": \"id2\"}, " +
          "\"delay\": {\"distribution\": \"constant\",\"mean\": \"100 ms\"}," +
          "\"httpStatusCode\": 200," +
          "\"response\": {\"id\": \"een\",\"someValue\": \"123123\"}" +
          "}" +
        "}"
      val postResponse: Response = Await.result(clientManager(postRequest))

      assert(postResponse.status == Status.Ok)
      assert(postResponse.contentString == "doublepathparam/$1/responsedata/$2")

      Then(s"the endpoint should respond with the posted response")
      val requestAfter = Request(Method.Get, newParametersUrl)
      requestAfter.host = serverAddress
      val responseAfter: Response = Await.result(clientServer(requestBefore))

      assert(responseAfter.status == Status.Ok)
      assert(responseAfter.contentString == "{\"id\":\"een\",\"someValue\":\"123123\"}")
    }
  }

  //given that the stubserver is running with a stub defined at endpoint "statefullstub"
  //when I do a HTTP POST to .../castalia/manager/stubs/responses with a
  // payload containing the stub endpoint {stubEndpoint} a stub response with id {statefullStubId}
  //then that stub response should be the response after I do a HTTP GET to o .../castalia/stubs/stubEndpoint/{statefullStubId}
}
