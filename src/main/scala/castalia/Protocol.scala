package castalia

import spray.json.DefaultJsonProtocol

case class Status(uptime: String)
case class Response(id: Option[Int], response: String)

case class StubResponse(id:String, httpStatusCode:StatusCode, response:AnyJsonObject)
case class StubEndpoint(endpoint: String, responses: List[StubResponse])

case class StaticEndpoint(endpoint: String, response: StaticResponse)
case class StaticResponse(status: Int, content: String)

trait Protocol extends DefaultJsonProtocol {
  implicit val statusFormatter = jsonFormat1(Status.apply)
  implicit val responseFormatter = jsonFormat2(Response.apply)
  implicit val StubResponseFormat = jsonFormat3(StubResponse)
  implicit val StubEndpointFormat = jsonFormat2(StubEndpoint)

}
