package castalia

import castalia.model.CastaliaConfig
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by jml on 12/12/15.
  */
class CastaliaConfigSpec extends WordSpec with Matchers  {
  "A CastaliaConfig object" when {

    "json file does not exist on classpath" should {
      "result in default port 9000 being configured" in {
        val config = CastaliaConfig.parse("nonExistingFile.json")
        config.httpPort.shouldBe(9000)
        config.managementPort.shouldBe(9090)
        config.stubs.shouldBe(Nil)
      }
    }

    "json file \"castalia.json\" exists on classpath" should {
      "return a CastaliaConfig object" in {
        val config = CastaliaConfig.parse("castalia.json")
        config.httpPort.shouldBe(9000)
        config.managementPort.shouldBe(9090)
        config.stubs.shouldBe(List("jsonconfiguredstub.json"))
      }
    }
  }
}
