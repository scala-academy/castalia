package castalia

import com.miguno.akka.testing.VirtualTime
import org.scalactic.TolerantNumerics
import org.scalatest.{Matchers, WordSpec}
import probability_monad.Distribution

import scala.concurrent.Future
import scala.concurrent.duration._
class DelayTraitSpec extends WordSpec with Matchers {

  "A Delay-trait" when {
    "called with duration" should {
      "use the scheduler" in {
        import scala.concurrent.ExecutionContext.Implicits.global

        val time = new VirtualTime
        implicit val scheduler = time.scheduler

        // Create anonymous class with trait mixed in, to test trait.
        val t = new {} with Delay{}
        val d = 20
        val delay = FiniteDuration(d, "ms")
        val result: Future[String] = t.future(Future("test"), delay)
        result.isCompleted shouldBe false
        time.advance((d-1).millis)
        result.isCompleted shouldBe false
        time.advance(1.millis)

        result.isCompleted shouldBe true
      }
    }
  }

  "A Distributed trait" when {
    "asked for a list of durations with a mean and a stdev" should {
      "follow the normal distribution" in {
        val t = new {} with DelayedDistribution {}
        val mean = 10.0
        val stdev = 2.0

        val dist = Distribution.normal
        val result: Distribution[Double] = t.distributedFuture(mean, stdev, dist)

        println(result.hist)
        result.mean shouldBe mean +- 0.1
        result.stdev shouldBe stdev +- 0.1
      }
    }
  }
}
