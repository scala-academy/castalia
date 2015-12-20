package castalia.model

import castalia.{AnyJsonObject, StatusCode}

import scala.concurrent.duration._

/**
  * Define all case classes for JSON responses.
  */

case class LatencyConfig(distribution:String, mean:String) {
  val time = mean.split(" ")(0).toInt
  val unit = mean.split(" ")(1) match {
    case "s" => SECONDS
    case "ms" => MILLISECONDS
  }

  def duration: FiniteDuration = FiniteDuration(time, unit)

}

case class ResponseConfig(id:String,
                          delay:Option[LatencyConfig],
                          httpStatusCode:StatusCode,
                          response:AnyJsonObject)

case class StubConfig(endpoint: String, responses: List[ResponseConfig])
