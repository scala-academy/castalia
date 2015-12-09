package castalia

import spray.json._

trait StubConfigParser extends Protocol {

def parseStubConfig( jsonFile: String): StubConfig = {
  scala.io.Source.fromFile(getClass.getResource("/"+jsonFile).getPath) //.fromInputStream(getClass.getResourceAsStream(jsonFile)) // read File
    .mkString // make it a string
    .parseJson // parse the string to Json objects
    .convertTo[StubConfig] // Convert to StubDef.
}
  def ReadStubInfo(jsonFiles: Array[String]) = {
    // Get all files from argumentlist
    val StubDefs = for (
      jsonFile <- jsonFiles // iterate over all jsonFiles
    ) yield
        parseStubConfig(jsonFile)

    val StubsByEndPoint: Map[Endpoint, Map[String, StubResponse2]] = StubDefs.map({
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
    StubsByEndPoint
  }
}
