package castalia

import castalia.model.StubConfig

case class StubConfigParser() extends Protocol {
  val converter = JsonConverter()

  def parseStubConfig(jsonFile: String): StubConfig = {
    converter.parseJson[StubConfig](jsonFile)
  }

  def parseStubConfigs(jsonFiles: List[String]): List[StubConfig] = {
    jsonFiles.map(parseStubConfig(_))
  }
}
