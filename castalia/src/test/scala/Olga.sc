import akka.http.scaladsl.server.{Route, RejectionHandler}
import castalia._
import castalia.model.ResponseConfig
import castalia.model.StubConfig
import spray.json.JsString
val stub = StubConfigParser.parseStubConfig("jsonconfiguredstub.json")
val stubsByEndpoints = StubConfigParser.readAndParseStubConfigFiles(List("jsonconfiguredstub.json"))

val responses = stubsByEndpoints.get("jsonconfiguredstub").get

responses("4") = (403, None)

responses

