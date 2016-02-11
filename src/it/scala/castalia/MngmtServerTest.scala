package castalia

import com.twitter.finagle
import com.twitter.finagle.http._
import com.twitter.io.Bufs._
import com.twitter.util.Await
import org.scalatest.DoNotDiscover
import utils.JsonUtil.stripIndent

@DoNotDiscover
class MngmtServerTest extends IntegrationTestBase {

  val client = finagle.Http.newService(s"$serverAddress")
  val mngmtClient = finagle.Http.newService(s"$mngmtAddress")
  val mngmtPath = "castalia/manager/endpoints"

  describe("starting up stubserver with 'castaliaT.json'") {

    it("should be possible to configure a new endpoint via manager endpoint") {

      val url = s"http://$mngmtAddress/$mngmtPath"
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
    mngmtRequest.host = mngmtAddress

    Then("the response should contain the metrics")
    var responseMng: Response = Await.result(mngmtClient(mngmtRequest))

    responseMng.status shouldBe Status.Ok
    responseMng.contentType.get shouldBe "application/json"
    assert(stripIndent(responseMng.contentString).contains(""""my/endpoint/$1": {"calls": 0}"""))

    val request = Request(Method.Get, "/my/endpoint/0")
    request.host = serverAddress

    When("Getting endpoint metrics after a GET on the endpoint")
    val responseStub: Response = Await.result(client(request))
    responseStub.status shouldBe Status.Ok

    Then("the response should contain updated stats")
    responseMng = Await.result(mngmtClient(mngmtRequest))

    responseMng.status shouldBe Status.Ok
    responseMng.contentType.get shouldBe "application/json"
    assert(stripIndent(responseMng.contentString).contains(""""my/endpoint/$1": {"calls": 1}"""))
  }

  it("should be possible to fetch metrics for a specific endpoint via manager") {
    val url = s"/$mngmtPath/metrics/my/endpoint/$$1"
    When(s"Getting endpoint metrics for one endpoint: $url")
    val mngmtRequest = Request(Method.Get, url)
    mngmtRequest.host = mngmtAddress

    Then("the response should contain the metrics")
    var responseMng: Response = Await.result(mngmtClient(mngmtRequest))

    responseMng.status shouldBe Status.Ok
    responseMng.contentType.get shouldBe "application/json"
    stripIndent(responseMng.contentString) shouldBe """{"metrics": {"my/endpoint/$1": {"calls": 1}}}"""
  }

  it("should get empty metrics for an endpoint that is not configured") {
    val url = s"/$mngmtPath/metrics/my/endpoint/that/does/not/exist"
    When(s"Getting endpoint metrics for non existing endpoint: $url")
    val mngmtRequest = Request(Method.Get, url)
    mngmtRequest.host = mngmtAddress

    Then("the response should not contain the metrics")
    var responseMng: Response = Await.result(mngmtClient(mngmtRequest))

    responseMng.status shouldBe Status.Ok
    responseMng.contentType.get shouldBe "application/json"
    stripIndent(responseMng.contentString) shouldBe """{"metrics": {"my/endpoint/that/does/not/exist": {}}}"""
  }
}