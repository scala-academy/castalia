package castalia.matcher

import akka.actor._
import akka.http.scaladsl.model.StatusCodes._
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import castalia.matcher.MatchResultGatherer.{MatchFound, MatchNotFound}
import castalia.model.Model.StubResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object MatchResultGatherer {

  case object MatchNotFound

  case class MatchFound(stubHandler: ActorRef, requestMatch: RequestMatch)

  def props(nOfMatchers: Int, origin: ActorRef): Props = Props(new MatchResultGatherer(nOfMatchers, origin))
}

class MatchResultGatherer(nrOfMatchers: Int, origin: ActorRef) extends Actor with ActorLogging {
  implicit val timeout = Timeout(2.seconds)

  def receive: Receive = awaitResponses(false, nrOfMatchers)

  def awaitResponses(responseSent: Boolean, responsesToGet: Int): Receive = {
    case MatchNotFound =>
      handleMatchNotFound(responseSent, responsesToGet)
    case MatchFound(handler, requestMatch) =>
      handleMatchFound(responseSent, responsesToGet, handler, requestMatch)
  }

  def handleMatchNotFound(responseSent: Boolean, responsesToGet: Int): Unit = {
    if (responsesToGet > 1) {
      // No match was found, wait for other matcher results
      context.become(awaitResponses(responseSent, responsesToGet - 1))
    } else {
      // This is the last match result we are waiting for. Stop this actor after processing this message
      context.stop(self)

      if (!responseSent) {
        // No response to origin was sent yet, do so now
        origin ! StubResponse(NotFound.intValue, NotFound.reason)
      }
    }
  }

  def handleMatchFound(responseSent: Boolean, responsesToGet: Int, stubHandler: ActorRef, requestMatch: RequestMatch): Unit = {
    if (!responseSent) {
      // First match result found, let stub handler create response and return that to consumer
      log.debug(s"match found for $stubHandler: $requestMatch")
      (stubHandler ? requestMatch) pipeTo origin
    }
    if (responsesToGet > 1) {
      // Other matchers will still send their result to this actor, stay alive until they have done so
      context.become(awaitResponses(true, responsesToGet - 1))
    } else {
      // No more matchers will send their result
      context.stop(self)
    }
  }
}
