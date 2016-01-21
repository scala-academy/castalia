//package castalia
//
//import akka.actor.ActorSystem
//import akka.http.scaladsl.model.StatusCodes.NotFound
//import akka.http.scaladsl.server.Directives._
//import akka.http.scaladsl.server.PathMatcher._
//import akka.http.scaladsl.server.Route
//import castalia.model.Model.{ResponseConfig, StubConfig}
//import spray.json._
//
//class StubService(stubConfigs: List[StubConfig])(implicit val system: ActorSystem) extends Routes {
//  protected val serviceName = "StubRoutes"
//
//  private var responseByEndpoint = (for {
//    stubConfig <- stubConfigs
//    response <- stubConfig.responses
//  } yield resolveEndpoint(stubConfig.endpoint, response.ids) -> response).toMap
//
//  if (responseByEndpoint.size != stubConfigs.map(_.responses.size).sum) {
//    throw new IllegalArgumentException("Duplicate endpoints have been defined")
//  }
//
//  protected lazy val dynamicStubRoutes = {
//    def createRoute: Route =
//      get {
//        path(Rest) {
//          path =>
//            responseByEndpoint.get(path) match {
//              case Some(responseConfig) =>
//                responseConfig.response match {
//                  case Some(content) => complete(responseConfig.httpStatusCode, content.toJson)
//                  case _ => complete(responseConfig.httpStatusCode, "")
//                }
//              case None => complete(NotFound, NotFound.reason)
//            }
//        }
//      } ~
//        post {
//          pathSuffix("responses") {
//            extractUnmatchedPath { path =>
//              entity(as[ResponseConfig]) {
//                rc =>
//                  responseByEndpoint += (path.toString() -> rc)
//                  complete("")
//              }
//            }
//
//          }
//        }
//
//    if (responseByEndpoint.isEmpty) {
//      log.info("No StubConfigs given")
//      reject
//    } else {
//      createRoute
//    }
//
//  }
//
//  def resolveEndpoint(endpoint: String, ids: EndpointIds): String = {
//    ids match {
//      case Some(vars) =>
//        vars.foldLeft(endpoint) {
//          (result, tuple: (String, String)) => result.replaceAll(s"\\${tuple._1}", tuple._2)
//        }
//      case _ => endpoint
//    }
//  }
//
//  override def routes: Route = {
//    pathPrefix("stubs") {
//      handleRejections(totallyMissingHandler) {
//        dynamicStubRoutes
//      }
//    }
//  }
//
//}
