package castalia

import akka.event.{ NoLogging, LoggingAdapter }
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.Timeouts
import org.scalatest.{ WordSpec, Matchers }

trait ServiceTestBase extends WordSpec with Matchers with ScalatestRouteTest with Timeouts {
  protected def log: LoggingAdapter = NoLogging
}
