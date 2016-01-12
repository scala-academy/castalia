package castalia

import castalia.utils.TestHelpers
import org.scalatest.{BeforeAndAfterAll, Suites}

class IntegrationTestSuite extends Suites (
  new JsonConfiguredStubServerTest,
  new StubServerTest
) with BeforeAndAfterAll {

  val serverAddress = "localhost:9000"
  val server = Main

  override def beforeAll(): Unit = {
    if (!TestHelpers.isServerRunning(serverAddress)) {
      println("server is not running... spinning up.")
      server.main(Array("castaliaT.json"))
    }
  }
}
