package castalia

import com.miguno.akka.testing.VirtualTime
import probability_monad.Distribution

import scala.concurrent.Future
import scala.concurrent.duration._
class DelayTraitSpec extends UnitSpecBase {

  "A Delay-trait" when {
    "called with duration" should {
      "use the scheduler" in {
        import scala.concurrent.ExecutionContext.Implicits.global

        val time = new VirtualTime
        implicit val scheduler = time.scheduler

        // Create anonymous class with trait mixed in, to test trait.
        val t: Delay with Object = new Delay{}
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
        val result: Distribution[Double] = t.normalDistribution(mean, stdev, dist)

        // hist samples for N = 10000.
        println(result.hist)
        result.mean shouldBe mean +- 0.1
        result.stdev shouldBe stdev +- 0.1
      }
    }

    "asked for a gamma distribution with two probabilities" should {
      "return a gamma distribution" in {
        val p50 = 20
        val p90 = 300

        val d = new DelayedDistribution {}

        val (k, t) = d.gammaRatios(0.5, p50, 0.9, p90)

        k shouldBe 0.2672 +- 0.0001
        t shouldBe 376.0 +- 0.2
        val dist = d.gammaDistribution(k, t)
        println(dist.hist)
      }

      "have a mean equal to shape times scale" in {
        val p50 = 100
        val p90 = 800
        val d = new DelayedDistribution {}

        val (k, t) = d.gammaRatios(p50, p90)
        val dist = d.gammaDistribution(k, t)


        println(s"Gamma distribution with p50 $p50 and p90 $p90")
        println(dist.hist)

        dist.mean shouldBe (k * t) +- 20

      }
    }

    "asked for a weibull distribution with two probabilities" should {
      val tolerance = 0.0001
      "return a gamma distribution" in {
        val p95 = 6.57803
        val p99 = 7.32456
        val d = new DelayedDistribution {}

        val shape = 4.0
        val scale = 5.0
        val (g, b) = d.getWeibullParametersFromPercentiles(p95, p99)

        g shouldBe shape +- tolerance
        b shouldBe scale +- tolerance

        println(Distribution.weibull(g,b).hist)
      }
    }
  }
}
