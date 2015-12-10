package castalia

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

trait StubService extends BaseService with StubConfigParser {

  protected val serviceName = "StubService"

  def dynamicStubRoutes : Route = {
    def createRoute(endpoint: String, responses: ResponsesByRequest): Route = path(endpoint / IntNumber) { id =>
      get {
        val response: Option[(StatusCode, AnyJsonObject)] = responses.get(id.toString)

        response match {
          case Some((statuscode: StatusCode, optResponse: AnyJsonObject)) =>
            optResponse match {
              case Some(content) => complete(statuscode, content.toJson)
              case _ => complete(statuscode, "")
            }
          case _ => complete(/*akka.http.scaladsl.model.StatusCodes.ServerError*/501, "Unknown response")
        }
      }
    }

    if (Main.stubsByEndPoint.isEmpty) {
      reject
    } else {
      Main.stubsByEndPoint map { case (e, r) => createRoute(e, r) } reduceLeft(_ ~ _)
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
      staticEndpoints map { case (e) => createRoute(e) } reduceLeft(_ ~ _)
    }
  }

  val stubRoutes = pathPrefix("stubs") {
    handleRejections(totallyMissingHandler) {
      staticRoutes ~
        dynamicStubRoutes ~
        pathPrefix("dynamicdummystub") {
          path("default") {
            parameter("response") { anyString =>
              get {
                complete(Response(None, anyString))
              }
            }
          } ~
            path(IntNumber) { id =>
              parameter("response") { anyString =>
                get {
                  complete(Response(Some(id), anyString))
                }
              }
            }
        }
    }
  }
}
