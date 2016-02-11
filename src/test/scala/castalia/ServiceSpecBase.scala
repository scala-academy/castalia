package castalia

import akka.event.{ NoLogging, LoggingAdapter }
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{ WordSpec, Matchers }

//TODO: find out if we still need this
/**
  * Base trait for service tests
  */
trait ServiceSpecBase extends WordSpec with Matchers with ScalatestRouteTest {
  protected def log: LoggingAdapter = NoLogging
}
