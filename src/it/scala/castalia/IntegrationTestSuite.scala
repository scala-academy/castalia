package castalia

import castalia.utils.TestHelpers
import org.scalatest.{BeforeAndAfterAll, Suites}

class IntegrationTestSuite extends Suites (
  new StubServerTest,
  new MngmtServerTest
) with BeforeAndAfterAll {

  val serverAddress = "localhost:9000"
  val server = Main

  override def beforeAll(): Unit = {
    val tryServer: Boolean = TestHelpers.isServerRunning(serverAddress).getOrElse(false)
    if (!tryServer) {
      println("server is not running... spinning up.")
      server.main(Array("castaliaT.json"))
    }
  }
}
