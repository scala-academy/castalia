package castalia

import spray.json.DefaultJsonProtocol

case class Status(uptime: String)
case class Response(id: Option[Int], response: String)

case class ResponseConfig(id:String, httpStatusCode:StatusCode, response:AnyJsonObject)
case class StubConfig(endpoint: String, responses: List[ResponseConfig])

case class StaticEndpoint(endpoint: String, response: StaticResponse)
case class StaticResponse(status: Int, content: String)

trait Protocol extends DefaultJsonProtocol {
  implicit val statusFormatter = jsonFormat1(Status.apply)
  implicit val responseFormatter = jsonFormat2(Response.apply)
  implicit val ResponseConfigFormat = jsonFormat3(ResponseConfig)
  implicit val StubConfigFormat = jsonFormat2(StubConfig)

}
