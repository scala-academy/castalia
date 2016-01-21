package castalia

import castalia.model.Model.StubConfig


object StubConfigParser {

  def parseStubConfig(jsonFile: String): StubConfig = {
    JsonConverter.parseJson[StubConfig](jsonFile)
  }

  def parseStubConfigs(jsonFiles: List[String]): List[StubConfig] = {
    jsonFiles.map(parseStubConfig(_))
  }
}
