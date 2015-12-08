package castalia

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.JsValue

/**
  * Created by Jens Kat on 27-11-2015.
  */
trait StubService extends BaseService with StubData {

  protected val serviceName = "StubService"

  def DynamicStubRoutes = {
    def createRoute(endpoint: String, responses: Map[String, (Int, Option[Map[String, JsValue]])]): Route = path(endpoint / IntNumber) { id =>
      get {
        val response: Option[(Int, Option[Map[String, JsValue]])] = responses.get(id.toString)

        response match {
          case Some((statuscode: Int, optResponse: Option[Map[String, JsValue]])) =>
            optResponse match {
              case Some(response) => complete(statuscode, response.toJson)
              case _ => complete(statuscode, "") //
            }
          case _ => complete(501, "Unknown response")
        }
      }
    }

    StubsByEndPoint.tail.foldLeft(createRoute(StubsByEndPoint.head._1, StubsByEndPoint.head._2))(
      (r, stub) => r ~ createRoute(stub._1, stub._2)
    )
  }

  val staticEndpoints = List(
    StaticEndpoint("hardcodeddummystub", StaticResponse(200, "Yay!")),
    StaticEndpoint("anotherstub", StaticResponse(200, "Different response")))

  def StaticRoutes: Route = {
    def createRoute(ep: StaticEndpoint): Route = path(ep.endpoint) {
      get {
        complete(ep.response.status, ep.response.content)
      }
    }

    staticEndpoints.tail.foldLeft(createRoute(staticEndpoints.head)) {
      (r, stub) => r ~ createRoute(stub)
    }
  }

  val stubRoutes = pathPrefix("stubs") {
    handleRejections(totallyMissingHandler) {
      StaticRoutes ~
        DynamicStubRoutes ~
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
