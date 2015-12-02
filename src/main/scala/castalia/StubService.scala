package castalia

import akka.http.scaladsl.server.Directives._

/**
  * Created by Jens Kat on 27-11-2015.
  */
trait StubService extends BaseService {

  protected val serviceName = "StubService"

  val stubRoutes = pathPrefix("stubs") {
    handleRejections(totallyMissingHandler) {
      path("hardcodeddummystub") {
        get {
          complete("Yay!")
        }
      } ~
      path("anotherstub") {
        get {
          complete("Different response")
        }
      } ~
      pathPrefix("dynamicdummystub"){
        path("default") {
          parameter("response") { anyString =>
              get {
                complete(Response(None, anyString))
              }
          }
        } ~
        path(IntNumber){ id =>
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
