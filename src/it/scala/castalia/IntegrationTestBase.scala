package castalia

import castalia.utils.TestHelpers
import org.scalatest.{BeforeAndAfterAll, FunSpec, GivenWhenThen, Matchers}


/**
 * Created by jens on 06-12-15.
 */
trait IntegrationTestBase extends FunSpec with GivenWhenThen with Matchers with BeforeAndAfterAll {
   // TODO make configurable
  val serverAddress = "localhost:9000"
  val server = Main

  override def beforeAll(): Unit = {
    if (!TestHelpers.isServerRunning(serverAddress)) {
      println("server is not running... spinning up.")
      server.main(Array("castaliaT.json"))
    }
  }
}
