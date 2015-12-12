package castalia

import java.io.FileNotFoundException
import java.net.URL

import castalia.model.StubConfig
import spray.json._

object StubConfigParser extends Protocol {

  def parseStubConfig(jsonFile: String): StubConfig = {
    val resource: URL = getClass.getResource("/" + jsonFile)
    resource match {
      case url : URL =>
        scala.io.Source.fromFile(resource.getPath) //.fromInputStream(getClass.getResourceAsStream(jsonFile)) // read File
          .mkString // make it a string
          .parseJson // parse the string to Json objects
          .convertTo[StubConfig] // Convert to StubDef.

      case _ => throw new FileNotFoundException(jsonFile)
    }
  }

  def readAndParseStubConfigFiles(jsonFiles: Array[String]): Map[Endpoint, ResponsesByRequest] = {
    // Get all files from argumentlist
    val stubConfigs = for (
      jsonFile <- jsonFiles // iterate over all jsonFiles
    ) yield
        parseStubConfig(jsonFile)

    val stubsConfigsByEndpoint: Map[Endpoint, ResponsesByRequest] = stubConfigs.map({
      // Create an outer map by endpoint
      s => (
        s.endpoint, // Endpoint is the outer key
        s.responses.map({
          // Create an inner map by repsonse id
          r => (
            r.id, // Id is the inner key
            (r.httpStatusCode, r.response))
        }).toMap // this is the outer map value (a map of id -> responses)
        )
    }).toMap // this is the map of all stubs (a map of endpoint -> (a map of id -> responses) )
    stubsConfigsByEndpoint
  }
}
