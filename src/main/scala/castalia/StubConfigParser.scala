package castalia

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import castalia.model.{CastaliaConfig, StubConfig}


object StubConfigParser extends Protocol {

  def parseStubConfig(jsonFile: String): StubConfig = {
   JsonConverter.parseJson[StubConfig](jsonFile)
  }

  def parseStubConfigs(jsonFiles: List[String]): List[StubConfig] = {
    jsonFiles.map(parseStubConfig(_))
  }
}
