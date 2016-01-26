package castalia.model

import akka.actor.ActorRef
import castalia._
import castalia.matcher.UriParser
import castalia.matcher.types.Segments
import spray.json.DefaultJsonProtocol

import scala.concurrent.duration.{FiniteDuration, Duration}

object Model extends DefaultJsonProtocol  {
  case class StubConfig(endpoint: String, responses: List[ResponseConfig]) {
    def segments: Segments = stringToSegments(endpoint)

    private def stringToSegments(input: String): Segments = {
      new UriParser().parse(input).pathList
    }
  }

  case class ResponseConfig(
      ids:EndpointIds,
      delay:Option[LatencyConfig],
      httpStatusCode:StatusCode,
      response:AnyJsonObject)

  case class LatencyConfig(distribution:String, mean:String) {
    def duration: Duration = Duration(mean)
  }

  case class CastaliaStatusResponse(uptime: Long)

  case class StaticEndpoint(endpoint: String, response: StaticResponse)

  case class StaticResponse(status: StatusCode, content: String)

  case class JsonFilesConfig(stubs: Array[String])

  case class StubResponse( status: StatusCode, body: String)

  case class DelayedResponse( destination: ActorRef, response: StubResponse, delay: LatencyConfig)

  implicit val castaliaStatusResponseFormatter = jsonFormat1(CastaliaStatusResponse)
  implicit val latencyConfigFormat = jsonFormat2(LatencyConfig)
  implicit val responseConfigFormat = jsonFormat4(ResponseConfig)
  implicit val stubConfigFormat = jsonFormat2(StubConfig)
  implicit val jsonFilesConfigFormat = jsonFormat1(JsonFilesConfig)
}