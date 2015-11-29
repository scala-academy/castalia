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
      path("dynamicdummystub" / "default") {
        parameter("response") {
          anyString =>
          get {
            complete(Response(anyString))
          }
        }
      }
    }
  }

}
