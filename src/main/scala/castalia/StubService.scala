package castalia

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class StubService(theStubsByEndpoints: StubConfigsByEndpoint)(implicit val system: ActorSystem) extends Routes {
  protected val serviceName = "StubRoutes"

  protected def stubsByEndpoints: StubConfigsByEndpoint = theStubsByEndpoints

  protected lazy val dynamicStubRoutes = {
    def createRoute(endpoint: String, responses: ResponsesByRequest): Route = path(endpoint / Segment) { id =>
      get {
        val response: Option[StubResponse] = responses.get(id)

        response match {
          case Some((statusCode: StatusCode, optResponse: AnyJsonObject)) =>
            optResponse match {
              case Some(content) => complete(statusCode, content.toJson)
              case _ => complete(statusCode, "")
            }
          case _ => complete(501, "Unknown response")
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
