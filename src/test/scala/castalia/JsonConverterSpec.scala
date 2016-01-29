package castalia

import java.io.FileNotFoundException

import castalia.model.Model.{ResponseProviderConfig, LatencyConfig, ResponseConfig, StubConfig}
import org.scalatest.{Matchers, WordSpec}

class JsonConverterSpec extends UnitSpecBase {

  "A JsonConverter object" when {

    "parsing a existing json file to a matching type" should {
      "return correctly parsed responses object" in {
        val stubconfig = JsonConverter.parseJson[StubConfig]("jsonconfiguredstub.json")
        stubconfig.endpoint.shouldBe("doublepathparam/$1/responsedata/$2")
        stubconfig.responses.isDefined.shouldBe(true)
        stubconfig.responses match {
          case Some(responses: List[ResponseConfig]) =>
            responses.size.shouldBe(4)
            responses(0).ids.shouldBe(Some(Map("1" -> "1", "2" -> "id1")))
            responses(0).delay.shouldBe(Some(LatencyConfig("constant", "100 ms")))
            responses(0).httpStatusCode.shouldBe(200)
          case None => assert(false, "has no response")
        }
        stubconfig.responseprovider.isDefined.shouldBe(false)
      }
    }

    "parsing a existing json file to a matching type" should {
      "return correctly parsed responseProvider object" in {
        val stubconfig = JsonConverter.parseJson[StubConfig]("jsonprogrammedstub.json")
        stubconfig.endpoint.shouldBe("somepath/$1/with/$2")
        stubconfig.responses.isDefined.shouldBe(false)
        stubconfig.responseprovider.isDefined.shouldBe(true)
        stubconfig.responseprovider match {
          case Some(responseProvider: ResponseProviderConfig) =>
            responseProvider.`class`.shouldBe("castalia.plugins.ProgrammedStub")
            responseProvider.member.shouldBe("process1")
          case None => assert(false, "has no responseprovider")
        }
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
