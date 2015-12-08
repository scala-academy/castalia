package castalia

import akka.http.scaladsl.server.Directives._

/**
  * Created by jml on 11/29/15.
  */
trait Routes extends BaseService with StatusService with StubService {

  override implicit protected val serviceName: String = "Routes"

  val routes = {
    statusRoutes ~
    stubRoutes
  }

}
