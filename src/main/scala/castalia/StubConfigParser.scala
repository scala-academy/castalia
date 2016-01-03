package castalia

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import castalia.model.{CastaliaConfig, StubConfig}


object StubConfigParser extends Protocol {



  def parseStubConfig(jsonFile: String): StubConfig = {
   JsonConverter.parseJson[StubConfig](jsonFile)
  }

  //TODO: is this method ever used?
  def parseConfigFile(configFile: String): JsonFilesConfig = {
    JsonConverter.parseJson[JsonFilesConfig](configFile)
  }

  def readAndParseStubConfigFiles(stubs : List[String]): Map[Endpoint, ResponsesByRequest] = {
    // Get all json files from the config file
    val stubConfigs: List[StubConfig] = stubs.map(parseStubConfig(_))

    val stubsConfigsByEndpoint: Map[Endpoint, ResponsesByRequest] = stubConfigs.map({
      // Create an outer map by endpoint
      s => (
        s.endpoint, // Endpoint is the outer key
        collection.mutable.Map[String, StubResponse]() ++ (s.responses.map({
          // Create an inner map by repsonse id
          r => (
            r.id, // Id is the inner key
            (r.httpStatusCode, r.response))
        }).toMap
        ) // this is the outer map value (a map of id -> responses)
        )
    }).toMap // this is the map of all stubs (a map of endpoint -> (a map of id -> responses) )

    if (stubConfigs.length == stubsConfigsByEndpoint.size) {
      stubsConfigsByEndpoint
    }
    else {
      throw new IllegalArgumentException("Duplicate endpoints have been defined")
    }
  }
}
