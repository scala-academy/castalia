package castalia

import java.io.FileNotFoundException

import castalia.model.{CastaliaConfig, ResponseConfig}

//import castalia.model.{StubConfig}
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
        responses.length === 3

        val req1Response = responses.find(r => r.id == "1").get
        req1Response.id === "1"
        req1Response.httpStatusCode === 200
        req1Response.response === Map[String, JsValue]("id" -> JsString("een"), "someValue" -> JsString("123123"))

        val req2Response = responses.find(r => r.id == "2").get
        req2Response.id === "2"
        req2Response.httpStatusCode === 200
        req2Response.response === Map[String, JsValue]("id" -> JsString("twee"),
          "someValue" -> JsString("123123"), "someAdditionalValue" -> JsString("345345"))

        val req0Response = responses.find(r => r.id == "0").get
        req0Response.id === "0"
        req0Response.httpStatusCode === 404
        req0Response.response === None

      }
    }

    "json file does not exist on classpath" should {
      "result in a StubConfigException thrown" in
        intercept[FileNotFoundException] {
          StubConfigParser.parseStubConfig("doesNotExistFile.json")
        }
    }

    "json file \"castalia.json\" exists on classpath" should {
      "return a CastaliaConfig object" in {
        val config = CastaliaConfig.parse("castalia.json")
        config.httpPort === 9000
      }
    }
  }
}
