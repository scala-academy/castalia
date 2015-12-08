package castalia

import spray.json._

case class StubResponse(id:String, httpStatusCode:Int, response:Option[Map[String, JsValue]])
case class StubEndpoint(endpoint: String, responses: List[StubResponse])

object StubDefProtocol extends DefaultJsonProtocol {
  implicit val StubResponseFormat = jsonFormat3(StubResponse)
  implicit val StubEndpointFormat = jsonFormat2(StubEndpoint)
}

case class StaticEndpoint(endpoint: String, response: StaticResponse)
case class StaticResponse(status: Int, content: String)

