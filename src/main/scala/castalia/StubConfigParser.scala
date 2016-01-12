package castalia

import castalia.model.StubConfig


object StubConfigParser extends Protocol {

  def parseStubConfig(jsonFile: String): StubConfig = {
   JsonConverter.parseJson[StubConfig](jsonFile)
  }

  def parseStubConfigs(jsonFiles: List[String]): List[StubConfig] = {
    jsonFiles.map(parseStubConfig(_))
  }
}
