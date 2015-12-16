package castalia

import castalia.model.{ResponseConfig, StubConfig}
import spray.json.DefaultJsonProtocol

case class CastaliaStatusResponse(uptime: Long)

case class StaticEndpoint(endpoint: String, response: StaticResponse)
case class StaticResponse(status: StatusCode, content: String)
case class JsonFilesConfig(stubs : Array[String])

trait Protocol extends DefaultJsonProtocol {
  implicit val castaliaStatusResponseFormatter = jsonFormat1(CastaliaStatusResponse)
  implicit val responseConfigFormat = jsonFormat3(ResponseConfig)
  implicit val stubConfigFormat = jsonFormat2(StubConfig)
  implicit val jsonFilesConfigFormat = jsonFormat1(JsonFilesConfig)
}
