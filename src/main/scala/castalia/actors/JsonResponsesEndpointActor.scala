package castalia.actors

import akka.actor._
import akka.http.scaladsl.model.StatusCodes.Forbidden
import akka.pattern.pipe
import castalia.matcher.RequestMatch
import castalia.matcher.types.Params
import castalia.model.Model.{StubResponse, _}
import castalia.{Delay, DelayedDistribution, EndpointIds}

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Actor that provides answers based on the responses listed in the json configuration that is used to create this actor
  *
  * Created on 2016-01-23
  */
class JsonResponsesEndpointActor(override val endpoint: String, val responses: List[ResponseConfig], override val metricsCollector: ActorRef)
  extends JsonEndpointActor
  with ActorLogging
  with Delay
  with DelayedDistribution {

  // We are assured that we are getting the dispatcher assigned to this actor
  // in case it differs from the main dispatcher for the system.
  import context._

  implicit val scheduler = system.scheduler
  override def receive: Receive = {
    case request: RequestMatch =>
      log.debug("receive requestmatch")

      // see if there is a response available for the parameters in the request
      val responseOption = findResponse(request.pathParams)

      responseOption match {
        case Some(response) =>
          log.debug("found a response")
          (response.response, response.delay) match {
            case (Some(content), Some(delay)) =>
              log.debug("make a delayed response with body")
              scheduleResponse(new StubResponse(response.httpStatusCode, content.toJson.toString),
                calculateDelayTime(delay), sender())
            case (Some(content), _) =>
              log.debug("make a immediate response with body")
              sender() ! new StubResponse( response.httpStatusCode, content.toJson.toString)
            case (_, Some(delay)) =>
              log.debug("make a delayed response without body")
              scheduleResponse(new StubResponse(response.httpStatusCode, ""),
                calculateDelayTime(delay), sender())
            case (_, _) =>
              log.debug("make an immediate empty response")
              sender() ! new StubResponse(response.httpStatusCode, "")
          }
        case _ =>
          log.debug("found no response")
          sender() ! new StubResponse( Forbidden.intValue, Forbidden.reason)
      }
    case _@msg =>
      log.error("received unexpected message [" + msg + "]")
  }

  def findResponse(pathParams: Params): Option[ResponseConfig] = {
    def findResponseRecurse(pathParams: Params, responses: List[ResponseConfig]): Option[ResponseConfig] =
      (pathParams, responses) match {
        case (_, Nil) => None
        case (params, (first :: rest)) => if (paramMatch(params, first.ids)) Some(first) else findResponseRecurse(params, rest)
        case (_, _) => None
      }
    findResponseRecurse(pathParams, responses)
  }

  def paramMatch( left: Params, right: EndpointIds): Boolean = {
    (left, right) match {
      case (Nil, None) => true
      case (left, None) => false
      case (Nil, Some(right)) => false
      case (left, Some(right)) => left.toMap == right
    }
  }

  def calculateDelayTime(latencyConfig: LatencyConfig): FiniteDuration = {
    latencyConfig.sample() match {
      case duration if duration.isFinite() =>
        log.debug("calculating delay for " + latencyConfig.duration.length + " " + latencyConfig.duration.unit)
        FiniteDuration(duration.length, duration.unit)
      // no match, we encountered a 'non-finite duration' let's set it to 0.
      case _ => FiniteDuration(0, MILLISECONDS)
    }
  }

  /**
    * Use the Delay-trait and pipe the message to the 'recipient'
    *
    * @param response The Stubresponse
    * @param delay The finiteduration of the delay
    * @param recipient The recipient, the sender of the request.
    */
  def scheduleResponse(response: StubResponse,
                       delay: FiniteDuration,
                       recipient: ActorRef): Unit =
    future(Future(response), delay) pipeTo recipient

}