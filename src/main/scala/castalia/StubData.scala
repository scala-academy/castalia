package castalia

import spray.json._
import StubDefProtocol._


trait StubData {
  val jsonFiles = Main.getJsonFiles
  // Get all files from argumentlist
  val StubDefs = for (
    jsonFile <- jsonFiles // iterate over all jsonFiles
  ) yield
      scala.io.Source.fromFile(jsonFile) //.fromInputStream(getClass.getResourceAsStream(jsonFile)) // read File
        .mkString // make it a string
      .parseJson // parse the string to Json objects
      .convertTo[StubEndpoint] // Convert to StubDef.

  val StubsByEndPoint: Map[String, Map[String, (Int, Option[Map[String, JsValue]])]] = StubDefs.map({
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
}
