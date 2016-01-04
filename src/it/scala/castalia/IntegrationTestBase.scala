package castalia

import org.scalatest._


/**
 * Created by jens on 06-12-15.
 */
trait IntegrationTestBase extends FunSpec with GivenWhenThen with Matchers {

  val serverAddress = "localhost:9000"
}
