package castalia

import spray.json.DefaultJsonProtocol

case class Status(uptime: String)
case class Response(response: String)

trait Protocol extends DefaultJsonProtocol {
  implicit val statusFormatter = jsonFormat1(Status.apply)
  implicit val responseFormatter = jsonFormat1(Response.apply)
}
