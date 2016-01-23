package castalia.actors

import akka.actor._
import akka.http.scaladsl.model.StatusCodes.Forbidden
import castalia.EndpointIds
import castalia.matcher.RequestMatch
import castalia.matcher.types.Params
import castalia.model.Model.{DelayedResponse, StubConfig, StubResponse, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Actor that provides answers based on the responses listed in the json configuration that is used to create this actor
  *
  * Created on 2016-01-23
  */
class JsonResponsesEndpointActor(myStubConfig: StubConfig) extends JsonEndpointActor(myStubConfig) {

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
              self ! new DelayedResponse(sender, new StubResponse(response.httpStatusCode, content.toJson.toString()), delay)
            case (Some(content), _) =>
              log.debug("make a immediate response with body")
              sender ! new StubResponse(response.httpStatusCode, content.toJson.toString)
            case (_, Some(delay)) =>
              log.debug("make a delayed response without body")
              self ! new DelayedResponse(sender, new StubResponse(response.httpStatusCode, ""), delay)
            case (_, _) =>
              log.debug("make an immediate empty response")
              sender ! new StubResponse(response.httpStatusCode, "")
          }
        case _ =>
          log.debug("found no response")
          sender ! new StubResponse(Forbidden.intValue, Forbidden.reason)
      }

    case delayedResponse: DelayedResponse =>
      log.debug("receive delayedresponse")
      context.system.scheduler.scheduleOnce(calculateDelayTime(delayedResponse.delay), self, new DelayComplete(delayedResponse.destination, delayedResponse.response))


    case delayComplete: DelayComplete =>
      log.debug("receive delaycomplete")
      delayComplete.destination ! delayComplete.message

    case x: Any =>
      log.debug("receive unexpected message [" + x + "]")
  }

  def findResponse(pathParams: Params): Option[ResponseConfig] = {
    def findResponseRecurse(pathParams: Params, responses: List[ResponseConfig]): Option[ResponseConfig] =
      (pathParams, responses) match {
        case (_, Nil) => None
        case (pathParams, first :: rest) => if (paramMatch(pathParams, first.ids)) Some(first) else findResponseRecurse(pathParams, rest)
        case (_, _) => None
      }
    myStubConfig.responses match {
      case Some(responseConfigs: List[ResponseConfig]) => findResponseRecurse(pathParams, responseConfigs)
      case None => None
    }
  }

  def paramMatch(left: Params, right: EndpointIds): Boolean = {
    (left, right) match {
      case (Nil, None) => true
      case (left, None) => false
      case (Nil, Some(right)) => false
      case (left, Some(right)) => left.toMap == right
    }
  }

  def calculateDelayTime(latencyConfig: LatencyConfig): FiniteDuration = {
    log.debug("calculating delay for " + latencyConfig.duration.length + " " + latencyConfig.duration.unit)
    (latencyConfig.duration, latencyConfig.duration.isFinite()) match {
      case (duration, true) => FiniteDuration(duration.length, duration.unit)
      case (_, _) => FiniteDuration(10, MILLISECONDS)
    }
  }
}
