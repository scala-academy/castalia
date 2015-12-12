package castalia.model

import java.net.URL

import spray.json._

/**
  * Created by M07H817 on 11-12-2015.
  */
case class CastaliaConfig(httpPort: Int)

object CastaliaConfig extends DefaultJsonProtocol {
  implicit val castaliaStatusResponseFormatter = jsonFormat1(CastaliaConfig.apply)

  def parse(config: String): CastaliaConfig = {
    val url = getClass.getResource("/" + config)
    url match {
      case url: URL =>
        scala.io.Source.fromURL(url)
          .mkString
          .parseJson
          .convertTo[CastaliaConfig]
      case _ => CastaliaConfig(9000)
    }
  }
}
