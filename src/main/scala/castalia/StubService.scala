package castalia

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RequestContext, RouteResult, Route, StandardRoute}
import castalia.model.{LatencyConfig, ResponseConfig}
import spray.json.JsValue

import akka.pattern.after


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}

class StubService(theStubsByEndpoints: StubConfigsByEndpoint)(implicit val system: ActorSystem) extends Routes {
  protected val serviceName = "StubRoutes"

  protected def stubsByEndpoints: StubConfigsByEndpoint = theStubsByEndpoints

  protected lazy val dynamicStubRoutes = {
    def createRoute(endpoint: String, responses: ResponsesByRequest): Route =
      pathPrefix(endpoint) {
        post {
              path("responses") {
                entity(as[ResponseConfig]) {
                  rc =>
                    responses(rc.id) = (rc.delay, rc.httpStatusCode, rc.response)
                    complete("")
                }
              }
        } ~
        get {
          path(Segment) { id =>
            val response: Option[StubResponse] = responses.get(id)

            response match {
              case Some((Some(delay), statusCode: StatusCode, optResponse: AnyJsonObject)) =>
                optResponse match {
                  case Some(content) => complete(
                    after(delay.duration, system.scheduler)
                    (Future(statusCode, content.toJson)))
                  case _ => complete(
                    after(delay.duration, system.scheduler)
                    (Future(statusCode, "")))
                }
              case Some((None, statusCode: StatusCode, optResponse: AnyJsonObject)) =>
                optResponse match {
                  case Some(content) => complete(statusCode, content.toJson)
                  case _ => complete(statusCode, "")
                }

              case _ => complete(501, "Unknown response")
            }
          }
        }
      }

      if (stubsByEndpoints.isEmpty) {
        log.info("No StubConfigs given")
        reject
      } else {
        log.info(s"${stubsByEndpoints.size} StubConfigs given")
        stubsByEndpoints map { case (e, r) => createRoute(e, r) } reduceLeft (_ ~ _)
      }
  }

  override def routes: Route = {
    pathPrefix("stubs") {
      handleRejections(totallyMissingHandler) {
        dynamicStubRoutes
      }
    }
  }
}
