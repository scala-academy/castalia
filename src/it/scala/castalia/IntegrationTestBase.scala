package castalia

import castalia.utils.TestHelpers
import com.twitter.util.Try
import org.scalatest.{BeforeAndAfterAll, FunSpec, GivenWhenThen, Matchers}


/**
 * Created by jens on 06-12-15.
 */
trait IntegrationTestBase extends FunSpec with GivenWhenThen with Matchers with BeforeAndAfterAll {
   // TODO make configurable
  val serverAddress = "localhost:9000"
  val server = Main

  override def beforeAll(): Unit = {
    val tryServer: Try[Boolean] = TestHelpers.isServerRunning(serverAddress)
    if (!tryServer.getOrElse(false)) {
      println("server is not running... spinning up.")
      server.main(Array("castaliaT.json"))
    }
  }
}
