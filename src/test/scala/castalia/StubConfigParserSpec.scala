package castalia

import java.io.FileNotFoundException
import java.util.concurrent.TimeUnit

import castalia.model.{LatencyConfig, CastaliaConfig, ResponseConfig}

import scala.concurrent.duration._

import org.scalatest.{Matchers, WordSpec}
import spray.json.{JsString, JsValue}

/**
  * Created by mihaelaoprea on 02/12/15.
  */
class StubConfigParserSpec extends WordSpec with Matchers {

  "A StubConfigParser" when {
    "json file \"jsonconfiguredstub.json\" exists on classpath" should {
      "return a StubConfig object" in {

        val stub = StubConfigParser.parseStubConfig("jsonconfiguredstub.json")

        stub.endpoint shouldBe "jsonconfiguredstub"
        val responses: List[ResponseConfig] = stub.responses
        assert(responses.length === 3)

        val req1Response = responses.find(r => r.id == "1").get
        req1Response.id shouldBe "1"
        req1Response.httpStatusCode shouldBe 200
        req1Response.delay shouldBe Some(LatencyConfig("constant", "100 ms"))
        req1Response.delay.get.duration shouldBe 100.millis
        req1Response.response shouldBe Some(Map[String, JsValue]("id" -> JsString("een"), "someValue" -> JsString("123123")))

        val req2Response = responses.find(r => r.id == "2").get
        req2Response.id shouldBe "2"
        req2Response.httpStatusCode shouldBe 200
        req2Response.delay shouldBe None
        req2Response.response shouldBe Some(Map[String, JsValue]("id" -> JsString("twee"),
          "someValue" -> JsString("123123"), "someAdditionalValue" -> JsString("345345")))

        val req0Response = responses.find(r => r.id == "0").get
        req0Response.id shouldBe "0"
        req0Response.delay shouldBe Some(LatencyConfig("constant", "2 s"))
        req0Response.delay.get.duration shouldBe 2.seconds
        req0Response.httpStatusCode shouldBe 404
        req0Response.response shouldBe None

      }
    }

    "json file does not exist on classpath" should {
      "result in a StubConfigException thrown" in
        intercept[FileNotFoundException] {
          StubConfigParser.parseStubConfig("doesNotExistFile.json")
        }
    }

    "duplicate endpoints exist in stubconfig" should {
      "result in a IllegalArgumentException" in {
        intercept[IllegalArgumentException] {
          StubConfigParser.readAndParseStubConfigFiles(
            List("jsonconfiguredstub.json","jsonconfiguredstub.json")
          )
        }
      }
    }

  }
}
