package castalia

import castalia.model.CastaliaConfig

class CastaliaConfigSpec extends UnitSpecBase {

  "A CastaliaConfig object" when {

    "parsing a json file that does not exist on classpath" should {
      "result in default ports being configured" in {
        val config = CastaliaConfig.parse("nonExistingFile.json")
        config.httpPort.shouldBe(9000)
        config.managementPort.shouldBe(9090)
        config.stubs.shouldBe(Nil)
      }
    }

    "parsing a json file \"castalia.json\" that exists on classpath" should {
      "result in CastaliaConfig object with values from the file" in {
        val config = CastaliaConfig.parse("castalia.json")
        config.httpPort.shouldBe(9000)
        config.managementPort.shouldBe(9090)
        config.stubs.shouldBe(List("jsonconfiguredstub.json"))
      }
    }
  }
}
