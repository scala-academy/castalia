package castalia

import java.io.FileNotFoundException

import castalia.model.Model.{ResponseConfig, StubConfig}
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by jens on 13-12-15.
  *
  */
class JsonConverterSpec extends WordSpec with Matchers {
  // we have to supply the Protocol trait, since the jsonreaders are defined there.

  "A JSON file" should {
    "be parsed" when {
      "calling parseJson of type T for Stubconfig" in {
        val stubconfig = JsonConverter.parseJson[StubConfig]("jsonconfiguredstub.json")
        assert(stubconfig.endpoint === "doublepathparam/$1/responsedata/$2")
      }
    }
    "not be parsed" when {
      "calling parsejson for a non-existing file" in {
        intercept[FileNotFoundException] {
          JsonConverter.parseJson[StubConfig]("none-existing.json")
        }
      }
      "unmarshalling an invalid Json" in {
        intercept[UnmarshalException] {
          JsonConverter.parseJson[ResponseConfig]("jsonconfiguredstub.json")
        }
      }
    }
  }
}
