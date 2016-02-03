package castalia.model

import castalia._
import castalia.matcher.UriParser
import castalia.matcher.types.Segments
import probability_monad.Distribution
import spray.json.DefaultJsonProtocol

import scala.concurrent.duration.Duration
import scala.util.Try

/**
  * Definition of all case classes that are also represented as json strings/files.
  *
  */
object Model extends DefaultJsonProtocol {

  /**
    * Configuration for the stub server, providing the http port, manager port, and optional list of stub
    * configuration files to parse.
    *
    * @param httpPort Int port number to listen on for stub requests
    * @param managementPort Int port number to listen on for management requests
    * @param stubs List of json files to parse for endpoint configurations
    */
  case class CastaliaConfig(
                             httpPort: Int = 9000,
                             managementPort: Int = 9090,
                             stubs: List[String] = List())

  /**
    * Default clause for responses, providing defaults to be used for responses that do not provide these values
 *
    * @param delay optional LatencyConfig
    * @param httpStatusCode optional StatusCode
    * @param response optional AnyJsonObject to use as response
    */
  case class DefaultResponseConfig(
                                    delay: Option[LatencyConfig],
                                    httpStatusCode: Option[StatusCode],
                                    response: Option[AnyJsonObject])

   /**
    * Specification of the response latency to simulate
     *
     * @param distribution String with type of distribution
    * @param mean String with mean (quantity + time unit)
    */
    case class LatencyConfig(distribution: String, mean: Option[String], p50: Option[String], p90: Option[String])
     extends DelayedDistribution {

      def duration: Duration = Duration(mean.getOrElse("0 ms"))

      val dist: Distribution[Double] = {

        val p1 = Duration(p50.getOrElse("0 ms")).toMillis
        val p2 = Duration(p90.getOrElse("0 ms")).toMillis

        distribution match {
          case "gamma" =>
            val (k, t): (Double, Double) = gammaRatios(p1, p2)
            gammaDistribution(k, t)
          case "weibull" =>
            weibullDistribution(p1, p2)
          case _ => Distribution.always(duration.toMillis)
        }
      }

      def sample(): Duration = {
        dist.sample(1) match {
          case head::tail => Duration(head + "ms")
          case _ => Duration("0 ms")
        }
      }
    }


  /**
    * Configuration for a single response
 *
    * @param ids EndpointIds with values that identify this response
    * @param delay optional LatencyConfig
    * @param httpStatusCode StatusCode for this response
    * @param response AnyJsonObject to use as body for this response
    */
  case class ResponseConfig(
                             ids: EndpointIds,
                             delay: Option[LatencyConfig],
                             httpStatusCode: StatusCode,
                             response: AnyJsonObject)

  /**
    * Programmed response provider
 *
    * @param clazz String with scala class name
    * @param member String with method name
    */
  case class ResponseProviderConfig(clazz: String, member : String)

  /**
    * Json Configuration for an endpoint
 *
    * @param endpoint String with path that will be matched
    * @param default optional DefaultResponseConfig with defaults for all responses
    * @param responses optional List[ResponseConfig] with possible responses for the endpoint
    * @param responseprovider optional ResponseProviderConfig if the endpoint is to be linked to a programmed response
    */
  case class StubConfig(endpoint: String, default: Option[DefaultResponseConfig],
                        responses: Option[List[ResponseConfig]], responseprovider : Option[ResponseProviderConfig]) {
    def segments: Segments = stringToSegments(endpoint)

    private def stringToSegments(input: String): Segments = {
      new UriParser().parse(input).pathList
    }
  }

  /**
    * Actual response that will be returned to the caller
 *
    * @param status StatusCode of the response
    * @param body String with serialized version of the AnyJsonObject of the ResponseConfig
    */
  case class StubResponse( status: StatusCode, body: String)

  /**
    * Define a default configuration to use when parsing the json fails
    */
  object CastaliaConfig extends DefaultJsonProtocol {

    def parse(config: String): CastaliaConfig = {
      Try {
        JsonConverter.parseJson[CastaliaConfig](config)
      } getOrElse CastaliaConfig()
    }
  }

  // Note: these implicits mus tbe declared in the correct order (first the leaves, then the composing classes)
  implicit val castaliaConfigFormatter = jsonFormat3(CastaliaConfig.apply)
  implicit val latencyConfigFormat = jsonFormat(LatencyConfig, "distribution", "mean", "p1", "p2")
  implicit val responseProviderFormat = jsonFormat(ResponseProviderConfig, "class", "member")
  implicit val defaultResponseConfigFormat = jsonFormat3(DefaultResponseConfig)
  implicit val responseConfigFormat = jsonFormat4(ResponseConfig)
  implicit val stubConfigFormat = jsonFormat4(StubConfig)

}