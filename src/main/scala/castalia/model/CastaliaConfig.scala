package castalia.model

import castalia.JsonConverter
import spray.json._

import scala.util.Try

/**
  * Created by M07H817 on 11-12-2015.
  */
case class CastaliaConfig(httpPort: Int)

object CastaliaConfig extends DefaultJsonProtocol {
  implicit val castaliaStatusResponseFormatter = jsonFormat1(CastaliaConfig.apply)
  val defaultPort = 9000
  def parse(config: String): CastaliaConfig = {
    Try{
      JsonConverter.parseJson[CastaliaConfig](config)
    } getOrElse CastaliaConfig(defaultPort)
  }
}
