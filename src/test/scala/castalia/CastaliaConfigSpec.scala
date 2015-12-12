package castalia

import java.io.FileNotFoundException

import castalia.model.{CastaliaConfig, ResponseConfig}
import org.scalatest.{Matchers, WordSpec}
import spray.json.{JsString, JsValue}

/**
  * Created by jml on 12/12/15.
  */
class CastaliaConfigSpec extends WordSpec with Matchers  {
  "A CastaliaConfig object" when {

    "json file does not exist on classpath" should {
      "result in default port 9000 being configured" in {
        val config = CastaliaConfig.parse("nonExistingFile.json")
        config.httpPort === 9000
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
