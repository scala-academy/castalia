package castalia.model

import castalia.{EndpointIds, StatusCode, AnyJsonObject}

import scala.concurrent.duration.Duration

case class LatencyConfig(distribution:String, mean:String) {
  def duration = Duration(mean)
}

case class ResponseConfig(ids:EndpointIds, delay:Option[LatencyConfig],
                          httpStatusCode:StatusCode, response:AnyJsonObject)

case class StubConfig(endpoint: String, responses: List[ResponseConfig])
