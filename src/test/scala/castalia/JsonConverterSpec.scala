package castalia

import java.io.FileNotFoundException

import castalia.model.Model.{DefaultResponseConfig, LatencyConfig, ResponseConfig, StubConfig}
import castalia.model.Model.{ResponseProviderConfig, LatencyConfig, ResponseConfig, StubConfig}
import org.scalatest.{Matchers, WordSpec}

class JsonConverterSpec extends UnitSpecBase {

  "The Json Converter objects" when {

    "parsing a existing json stub config file to a matching type" should {
      "return correctly parsed object" in {
        val stubconfig = JsonConverter.parseJson[StubConfig]("jsonconfiguredstub.json")
        stubconfig.endpoint.shouldBe("doublepathparam/$1/responsedata/$2")
        stubconfig.responses.isDefined.shouldBe(true)
        stubconfig.responses match {
          case Some(responses: List[ResponseConfig]) =>
            responses.size.shouldBe(4)
            responses(0).ids.shouldBe(Some(Map("1" -> "1", "2" -> "id1")))
            responses(0).delay.shouldBe(Some(LatencyConfig("constant", "100 ms")))
            responses(0).httpStatusCode.shouldBe(200)
          case None => assert(false, "has no response definition")
        }
        stubconfig.responseprovider.isDefined.shouldBe(false)
      }
    }

    "parsing a existing json file to a matching type" should {
      "return correctly parsed responseProvider object" in {
        val stubconfig = JsonConverter.parseJson[StubConfig]("jsonprogrammedstub1.json")
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

    "parsing a stubconfig with default" should {
      "return correctly parsed stubconfig" in {
        val stubconfig = JsonConverter.parseJson[StubConfig]("sharedproperties.json")
        stubconfig.endpoint.shouldBe("somestub/$parm")
        stubconfig.default.shouldBe(Some(DefaultResponseConfig(Some(LatencyConfig("constant", "100 ms")), None, None)))
        stubconfig.responses match {
          case Some(responses: List[ResponseConfig]) =>
            responses.head.ids.shouldBe(Some(Map(("parm", "1"))))
          case None => assert(false, "has no response definition")

        }
      }
    }
  }
}
