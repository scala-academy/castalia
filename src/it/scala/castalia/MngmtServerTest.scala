package castalia

import com.twitter.finagle
import com.twitter.finagle.http._
import com.twitter.io.Bufs._
import com.twitter.util.Await
import org.scalatest.DoNotDiscover

@DoNotDiscover
class MngmtServerTest extends IntegrationTestBase {

  val client = finagle.Http.newService(s"$serverAddress")
  val mngmtClient = finagle.Http.newService(s"$managerAddress")
  val mngmtPath = "castalia/manager/endpoints"

  describe("starting up stubserver with 'castaliaT.json'") {

    it("should be possible to configure a new endpoint via manager endpoint") {

      val url = s"http://$managerAddress/$mngmtPath"
      val data = """{"endpoint": "my/endpoint/$1", "responses": [{"ids": {"1": "0"},"httpStatusCode": 200}]}"""

      When(s"""I do a HTTP POST to "$url" with new endpoint """)

      val mngmtRequest = RequestBuilder().url(url).setHeader("Content-Type", "application/json").buildPost(utf8Buf(data))

      Then("the response should be new endpoint")
      val response: Response = Await.result(mngmtClient(mngmtRequest))

      response.status shouldBe Status.Ok
      response.contentString shouldBe "my/endpoint/$1"
    }
  }

  it("should be possible to fetch metrics via manager endpoint") {

    When("Getting endpoint metrics")
    val mngmtRequest = Request(Method.Get, s"/$mngmtPath/metrics")
    mngmtRequest.host = managerAddress

    Then("the response should contain the metrics")
    var responseMng: Response = Await.result(mngmtClient(mngmtRequest))

    responseMng.status shouldBe Status.Ok
    responseMng.contentType.get shouldBe "application/json"
    responseMng.contentString.contains(""""my/endpoint/$1": {
                                         |      "calls": 0
                                         |    }""")

    val request = Request(Method.Get, "/my/endpoint/0")
    request.host = serverAddress

    When("Getting endpoint metrics after a GET on the endpoint")
    val responseStub: Response = Await.result(client(request))
    responseStub.status shouldBe Status.Ok

    Then("the response should contain updated stats")
    responseMng = Await.result(mngmtClient(mngmtRequest))

    responseMng.status shouldBe Status.Ok
    responseMng.contentType.get shouldBe "application/json"
    responseMng.contentString.contains(""""my/endpoint/$1": {
                                         |      "calls": 1
                                         |    }""")
  }

}