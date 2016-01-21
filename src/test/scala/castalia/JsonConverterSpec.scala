package castalia

import java.io.FileNotFoundException

import castalia.model.Model.{DefaultResponseConfig, LatencyConfig, ResponseConfig, StubConfig}
import org.scalatest.{Matchers, WordSpec}

class JsonConverterSpec extends UnitSpecBase {

  "The Json Converter objects" when {

    "parsing a existing json stub config file to a matching type" should {
      "return correctly parsed object" in {
        val stubconfig = JsonConverter.parseJson[StubConfig]("jsonconfiguredstub.json")
        stubconfig.endpoint.shouldBe("doublepathparam/$1/responsedata/$2")
        stubconfig.responses.size.shouldBe(4)
        stubconfig.responses(0).ids.shouldBe(Some(Map("1" -> "1", "2" -> "id1")))
        stubconfig.responses(0).delay.shouldBe(Some(LatencyConfig("constant", "100 ms")))
        stubconfig.responses(0).httpStatusCode.shouldBe(200)
      }
    }

    "parsing a non-existing file" should {
      "throw FileNotFoundException" in {
        intercept[FileNotFoundException] {
          JsonConverter.parseJson[StubConfig]("none-existing.json")
        }
      }
    }

    "parsing an existing json file to a wrong type" should {
      "throw UnmarshalException" in {
        intercept[UnmarshalException] {
          JsonConverter.parseJson[ResponseConfig]("jsonconfiguredstub.json")
        }
      }
    }

    "parsing a stubconfig with default" should {
      "return correctly parsed stubconfig" in {
        val stubConfig = JsonConverter.parseJson[StubConfig]("sharedproperties.json")

        stubConfig.endpoint.shouldBe("somestub/$parm")
        stubConfig.default.shouldBe(Some(DefaultResponseConfig(Some(LatencyConfig("constant", "100 ms")), None, None)))
      }
    }
  }
}
