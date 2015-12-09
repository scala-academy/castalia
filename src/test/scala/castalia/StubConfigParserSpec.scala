package castalia

import java.io.FileNotFoundException

//import castalia.model.{StubConfig}
import org.scalatest.{Matchers, WordSpec}
import spray.json.{JsString, JsValue}

/**
  * Created by mihaelaoprea on 02/12/15.
  */
class StubConfigParserSpec extends WordSpec with Matchers with StubConfigParser {

  "A StubConfigParser" when {
    "json file \"jsonconfiguredstub.json\" exists on classpath" should {
      "return a StubConfig object" in {

        val stub = parseStubConfig("jsonconfiguredstub.json")

        stub.endpoint shouldBe "jsonconfiguredstub"
        val responses = stub.responses
        responses.length === 3

        responses.head.id === "1"
        responses.head.httpStatusCode === 200
        responses.head.response === Map[String, JsValue]("id" -> JsString("een"), "someValue" -> JsString("123123"))

        responses(1).id === "2"
        responses(1).httpStatusCode === 200
        responses(1).response === Map[String, JsValue]("id" -> JsString("twee"),
          "someValue" -> JsString("123123"), "someAdditionalValue" -> JsString("345345"))

        responses(2).id === "0"
        responses(2).httpStatusCode === 404
        responses(2).response === None

      }
    }

    "json file does not exist on classpath" should {
      "result in a StubConfigException thrown" in
        intercept[FileNotFoundException] {
          parseStubConfig("doesNotExistFile.json")
        }
    }

    /*"json file \"castalia.json\" exists on classpath" should {
      "return a CastaliaConfig object" in {
        val config = StubConfigParser.toCastaliaConfig("castalia.json")
        config.httpPort === 9000
      }
    }*/
  }
}
