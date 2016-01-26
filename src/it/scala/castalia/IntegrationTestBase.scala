package castalia

import org.scalatest._


trait IntegrationTestBase extends FunSpec with GivenWhenThen with Matchers {

  val serverAddress = "localhost:9000"
  val managerAddress = "localhost:9090"

}
