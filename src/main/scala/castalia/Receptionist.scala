package castalia

import akka.http.scaladsl.server.StandardRoute

import scala.concurrent.Future
import scala.util.{Failure, Success}
import akka.actor.Actor.Receive
import akka.actor._
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import akka.pattern.ask
import akka.pattern.pipe
import spray.http.Uri.{Query, Path}
import scala.concurrent.duration._
import spray.routing.{RequestContext, HttpService}

object Receptionist {
  case class UpSertEndpoint(stubConfigJSON: String)
  case class CreateEndPointActor()

//  def props(x: ActorRef): Props = Props(new Receptionist(x))
}


class Receptionist extends Actor with HttpService with ActorLogging {

  import  Receptionist._

  implicit def executionContext = actorRefFactory.dispatcher
  implicit val timeout = Timeout(5.seconds)

  val endpointActor = actorRefFactory.actorOf(EndpointActor.props, "default")

  def actorRefFactory = context

  override def receive: Receive = {
    case UpSertEndpoint(stubConfigJSON: String) =>
      log.info(s"UpSertEndpoint.")
    case CreateEndPointActor() =>
      log.info(s"CreateEndPointActor.")
    case _   =>
      log.info(s"stubsRoute.")
      runRoute(stubsRoute)
  }


  def getActor(path : String) : ActorRef = {
    val uriSegments = path.split("/")

    val actor = if (uriSegments.length ==0) {
      endpointActor
    } else {
      println("eerste path" + uriSegments(1))
      endpointActor // some work here
    }
    actor
  }

  val stubsRoute = {
    get {
      extract(_.request) { request =>
        val eventualResponse = (getActor(request.uri.path.toString()) ? request)
        onSuccess(eventualResponse) {
          response => {
            complete(response.asInstanceOf[String])
          }
        }
      }
    }
  }
}


