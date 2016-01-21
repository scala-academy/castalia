package castalia

import castalia.StubConfigParser._
import castalia.model.Model.LatencyConfig

/**
  * Created by Jean-Marc van Leerdam on 2016-01-21
  */
class StubConfigParserTest extends UnitSpecBase {

  "StubConfigParser$Test" should {

    "parseStubConfig processes normal json " in {
      val stubConfig = parseStubConfig("jsonsimplestub.json")
      stubConfig.endpoint.shouldBe("somepath/$1")
      stubConfig.responses.size.shouldBe(3)
      stubConfig.responses(0).ids.shouldBe(Some(Map("1" -> "1")))
      stubConfig.responses(0).delay.shouldBe(Some(LatencyConfig("constant", "1000 ms")))
      stubConfig.responses(0).httpStatusCode.shouldBe(200)

    }

    "parseStubConfig processes shared properties json " in {
      val stubConfig = parseStubConfig("sharedproperties.json")
      stubConfig.endpoint.shouldBe("somestub/$parm")
      stubConfig.responses.size.shouldBe(3)
      stubConfig.responses(0).ids.shouldBe(Some(Map("parm" -> "1")))
      stubConfig.responses(0).delay.shouldBe(Some(LatencyConfig("constant", "100 ms")))
      stubConfig.responses(0).httpStatusCode.shouldBe(200)
      stubConfig.responses(1).ids.shouldBe(Some(Map("parm" -> "2")))
      stubConfig.responses(1).delay.shouldBe(Some(LatencyConfig("constant", "100 ms")))
      stubConfig.responses(1).httpStatusCode.shouldBe(200)
      stubConfig.responses(2).ids.shouldBe(Some(Map("parm" -> "0")))
      stubConfig.responses(2).delay.shouldBe(Some(LatencyConfig("constant", "2 s")))
      stubConfig.responses(2).httpStatusCode.shouldBe(404)



    }

  }
}
