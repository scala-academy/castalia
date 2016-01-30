package castalia

import castalia.StubConfigParser._
import castalia.model.Model.{ResponseConfig, LatencyConfig}

/**
  * Created by Jean-Marc van Leerdam on 2016-01-21
  */
class StubConfigParserTest extends UnitSpecBase {

  "StubConfigParser$Test" should {

    "parseStubConfig processes normal json " in {
      val stubConfig = parseStubConfig("jsonsimplestub.json")
      stubConfig.endpoint.shouldBe("somepath/$1")
      stubConfig.responses.isDefined.shouldBe(true)
      stubConfig.responses match {
        case Some(responses: List[ResponseConfig]) =>
          responses.size.shouldBe(3)
          responses(0).ids.shouldBe(Some(Map("1" -> "1")))
          responses(0).delay.shouldBe(Some(LatencyConfig("constant", "1000 ms")))
          responses(0).httpStatusCode.shouldBe(200)
        case None => assert(false, "has no response definition")
      }
      stubConfig.responseprovider.isDefined.shouldBe(false)
    }

    "parseStubConfig processes shared properties json " in {
      val stubConfig = parseStubConfig("sharedproperties.json")
      stubConfig.endpoint.shouldBe("somestub/$parm")
      stubConfig.responses.isDefined.shouldBe(true)
      stubConfig.responses match {
        case Some(responses: List[ResponseConfig]) =>
          responses.size.shouldBe(3)
          responses(0).ids.shouldBe(Some(Map("parm" -> "1")))
          responses(0).delay.shouldBe(Some(LatencyConfig("constant", "100 ms")))
          responses(0).httpStatusCode.shouldBe(200)
          responses(1).ids.shouldBe(Some(Map("parm" -> "2")))
          responses(1).delay.shouldBe(Some(LatencyConfig("constant", "100 ms")))
          responses(1).httpStatusCode.shouldBe(200)
          responses(2).ids.shouldBe(Some(Map("parm" -> "0")))
          responses(2).delay.shouldBe(Some(LatencyConfig("constant", "2 s")))
          responses(2).httpStatusCode.shouldBe(404)
        case None => assert(false, "has no response definition")
      }
      stubConfig.responseprovider.isDefined.shouldBe(false)
    }
  }
}
