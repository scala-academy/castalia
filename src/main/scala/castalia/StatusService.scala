package castalia

import java.lang.management.ManagementFactory

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.concurrent.duration._

trait StatusService extends BaseService {
  protected val serviceName = "my service"

  val statusRoutes: Route = pathPrefix("status") {
    handleRejections(totallyMissingHandler) {
      get {
        log.info("/status executed")
        complete(Status(Duration(ManagementFactory.getRuntimeMXBean.getUptime, MILLISECONDS).toString()))
      }
    }
  }
}
