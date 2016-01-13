package castalia.model

import castalia.JsonConverter
import spray.json._

import scala.util.Try

case class CastaliaConfig(httpPort: Int = 9000,
                          managementPort: Int = 9090,
                          stubs: List[String] = List())

object CastaliaConfig extends DefaultJsonProtocol {
  implicit val castaliaStatusResponseFormatter = jsonFormat3(CastaliaConfig.apply)

  def parse(config: String): CastaliaConfig = {
    Try {
      JsonConverter.parseJson[CastaliaConfig](config)
    } getOrElse CastaliaConfig()
  }
}
