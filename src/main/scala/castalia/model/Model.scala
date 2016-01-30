package castalia.model

import akka.actor.ActorRef
import castalia._
import castalia.matcher.UriParser
import castalia.matcher.types.Segments
import spray.json.DefaultJsonProtocol

import scala.concurrent.duration.{FiniteDuration, Duration}

object Model extends DefaultJsonProtocol  {
  case class ResponseProviderConfig(clazz: String, member : String)

  case class StubConfig(endpoint: String, default: Option[DefaultResponseConfig], responses: Option[List[ResponseConfig]], responseprovider : Option[ResponseProviderConfig]) {
    def segments: Segments = stringToSegments(endpoint)

    private def stringToSegments(input: String): Segments = {
      new UriParser().parse(input).pathList
    }
  }

  // used to provide default values for the responses
  case class DefaultResponseConfig(
      delay: Option[LatencyConfig],
      httpStatusCode: Option[StatusCode],
      response: Option[AnyJsonObject])

  case class ResponseConfig(
      ids: EndpointIds,
      delay: Option[LatencyConfig],
      httpStatusCode: StatusCode,
      response: AnyJsonObject)

  case class LatencyConfig(distribution:String, mean:String) {
    def duration: Duration = Duration(mean)
  }

  case class CastaliaStatusResponse(uptime: Long)

  case class StaticEndpoint(endpoint: String, response: StaticResponse)

  case class StaticResponse(status: StatusCode, content: String)

  case class JsonFilesConfig(stubs: Array[String])

  case class StubResponse( status: StatusCode, body: String)

  case class DelayedResponse( destination: ActorRef, response: StubResponse, delay: LatencyConfig)

  // Note: these implicits mus tbe declared in the correct order (first the leaves, then the composing classes)
  implicit val castaliaStatusResponseFormatter = jsonFormat1(CastaliaStatusResponse)
  implicit val latencyConfigFormat = jsonFormat2(LatencyConfig)
  implicit val responseProviderFormat = jsonFormat(ResponseProviderConfig, "class", "member")
  implicit val defaultResponseConfigFormat = jsonFormat3(DefaultResponseConfig)
  implicit val jsonFilesConfigFormat = jsonFormat1(JsonFilesConfig)
  implicit val responseConfigFormat = jsonFormat4(ResponseConfig)
  implicit val stubConfigFormat = jsonFormat4(StubConfig)
}