package castalia

import java.io.FileNotFoundException

import castalia.model.Model.{LatencyConfig, ResponseConfig, StubConfig}
import org.scalatest.{Matchers, WordSpec}

class JsonConverterSpec extends WordSpec with Matchers {

  "A JsonConverter object" when {

    "parsing a existing json file to a matching type" should {
      "return correctly parsed object" in {
        val stubconfig = JsonConverter.parseJson[StubConfig]("jsonconfiguredstub.json")
        stubconfig.endpoint.shouldBe("doublepathparam/$1/responsedata/$2")
        stubconfig.responses.size.shouldBe(4)
        stubconfig.responses(0).ids.shouldBe(Some(Map("$1" -> "1", "$2" -> "id1")))
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
    }
}
