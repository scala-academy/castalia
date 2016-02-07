package castalia.matcher

import akka.actor._
import akka.http.scaladsl.model.StatusCodes._
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import castalia.matcher.MatchResultGatherer.{MatchFound, MatchNotFound}
import castalia.model.Model.StubResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by Jordi on 4-2-2016.
  */
object MatchResultGatherer {

  case object MatchNotFound

  case class MatchFound(stubHandler: ActorRef, requestMatch: RequestMatch)

  def props(nOfMatchers: Int, origin: ActorRef): Props = Props(new MatchResultGatherer(nOfMatchers, origin))
}

class MatchResultGatherer(nrOfMatchers: Int, origin: ActorRef) extends Actor with ActorLogging {
  implicit val timeout = Timeout(2.seconds)

  def receive: Receive = awaitResponses(false, nrOfMatchers)

  def awaitResponses(responseSent: Boolean, responsesToGet: Int): Receive = {
    case MatchNotFound if responsesToGet > 1 => context.become(awaitResponses(responseSent, responsesToGet - 1))
    case MatchNotFound =>
      origin ! StubResponse(NotFound.intValue, NotFound.reason)
      self ! Kill
    case MatchFound(handler, requestMatch) if !responseSent && responsesToGet > 0 =>
      (handler ? requestMatch) pipeTo origin
      context.become(awaitResponses(true, responsesToGet - 1))
    case MatchFound if responsesToGet > 1 =>
      context.become(awaitResponses(true, responsesToGet - 1))
    case _ =>
      self ! Kill
  }
}
