package castalia

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import castalia.model.ResponseConfig
import spray.json.JsString

trait StubService extends BaseService with StubConfigParser {

  protected val serviceName = "StubService"

  def dynamicStubRoutes: Route = {
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

    if (Main.stubsByEndPoint.isEmpty) {
      reject
    } else {
      Main.stubsByEndPoint map { case (e, r) => createRoute(e, r) } reduceLeft (_ ~ _)
    }
  }

  val staticEndpoints = List(
    StaticEndpoint("hardcodeddummystub", StaticResponse(200, "Yay!")),
    StaticEndpoint("anotherstub", StaticResponse(200, "Different response")))

  def staticRoutes: Route = {
    def createRoute(ep: StaticEndpoint): Route = path(ep.endpoint) {
      get {
        complete(ep.response.status, ep.response.content)
      }
    }

    if (staticEndpoints.isEmpty) {
      reject
    } else {
      staticEndpoints map { case (e) => createRoute(e) } reduceLeft (_ ~ _)
    }
  }

  val stubRoutes = pathPrefix("stubs") {
    handleRejections(totallyMissingHandler) {
      staticRoutes ~ dynamicStubRoutes
    }
  }
}
