package castalia

import java.lang.management.ManagementFactory

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

trait StatusService extends BaseService with Protocol {
  protected val serviceName = "my service"

  val statusRoutes: Route = pathPrefix("status") {
    handleRejections(totallyMissingHandler) {
      get {
        log.info("/status executed")
        complete(StatusCodes.OK, CastaliaStatusResponse(ManagementFactory.getRuntimeMXBean.getUptime))
      }
    }
  }
}
