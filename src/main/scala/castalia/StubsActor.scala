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

object StubsActor {
  case class UpdateConfig(configFile: String)
  case class Undeploy()
}


class StubsActor extends Actor with StubsService with ActorLogging {
  def actorRefFactory = context

//  var endpointActorsMap : Map[String, ActorRef] = Map()
//  val endpoints = Array("abc", "def") // Needs some work here
//
//  endpoints.foreach( endpoint => {
//                  endpointActorsMap += (endpoint -> actorRefFactory.actorOf(DefaultActor.props, endpoint) )
//                  })
//
// def getActorForEndpoint (endpoint: String): Option[ActorRef] = {
//   if (endpointActorsMap.contains(endpoint)) {
//     Some(endpointActorsMap(endpoint))
//   } else {
//     None
//   }
// }

  def receive: Receive = runRoute(stubsRoute)
}

trait StubsService extends HttpService {
  implicit def executionContext = actorRefFactory.dispatcher
  implicit val timeout = Timeout(5.seconds)


  val defaultActor = actorRefFactory.actorOf(DefaultActor.props, "default")

  def getActor(path : String) : ActorRef = {
    val uriSegments = path.split("/")

    val actor = if (uriSegments.length ==0) {
      defaultActor
    } else {
      println("eerste path" + uriSegments(1))
      defaultActor // some work here
    }

    actor
  }

  val stubsRoute = {
    get {
        extract(_.request) { request =>
          //val baseUri = request.withQuery(Query(None))
           //val eventualResponse = getActor(requestContext.request.uri.path) ? requestContext
          val eventualResponse = (getActor(request.uri.path.toString()) ?  request)
          onSuccess(eventualResponse) {
            response => {
              complete(response.asInstanceOf[String])
              }
          }
        }
    }

    }
}

