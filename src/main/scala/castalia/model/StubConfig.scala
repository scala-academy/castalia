package castalia.model

import castalia.{StatusCode, AnyJsonObject}

import scala.concurrent.duration.Duration

/**
  * Created by M07H817 on 11-12-2015.
  */

case class LatencyConfig(distribution:String, mean:String) {
  def duration = Duration(mean)
}
case class ResponseConfig(id:String,  delay:Option[LatencyConfig], httpStatusCode:StatusCode, response:AnyJsonObject)
case class StubConfig(endpoint: String, responses: List[ResponseConfig])
