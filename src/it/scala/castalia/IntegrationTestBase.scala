package castalia

import org.scalatest._


trait IntegrationTestBase extends FunSpec with GivenWhenThen with Matchers {

  val serverAddress = "localhost:9000"
  val mngmtAddress = "localhost:9090"
}
