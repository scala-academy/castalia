package castalia

import com.twitter.finagle
import com.twitter.finagle.http.{Method, Request}
import com.twitter.util.Await
import org.scalatest.DoNotDiscover

/**
  * Created by rezolya on 05/12/2015.
  */
@DoNotDiscover
class JsonConfiguredStubServerTest extends IntegrationTestBase {
  // TODO: other way than starting application using empty string array in main?

  describe("Stub server"){
    it("Should allow the user can configure endpoint name in json file") {

      info("background:")
      info("given that the stubserver is started and configured to use a stub called \"jsonconfiguredstub\"")
      info("and that a stub config file with name \"jsonconfiguredstub.json\" is on the classpath")
      //[Note: for now i use hardcoded StubConfig object in Routes]

      Given("jsonconfiguredstub.json contains a property \"endpoint\" equal to \"jsonconfiguredstub\"")

      val client = finagle.Http.newService(serverAddress)
      //TODO: make here a jsonconfiguredstub.json file and put it to classpath

      When("I do a HTTP GET to .../stubs/jsonconfiguredstub")
      val request = Request(Method.Get, "/status")
      request.host = serverAddress
      val response = Await.result(client(request))

      Then("then I should get a HTTP 200 response")
      assert(response.statusCode == 200)
    }
  }
}
