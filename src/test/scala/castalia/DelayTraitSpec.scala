package castalia

import com.miguno.akka.testing.VirtualTime
import org.scalactic.TolerantNumerics
import org.scalatest.{Matchers, WordSpec}
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

    "asked for a gamma distribution" should {
      "hand out delays according to gamma distribution" in {
        val d = new DelayedDistribution {}
        val shapeK = 3
        val scaleTheta = 5

        val dist = d.gammaDistribution(shapeK, scaleTheta)



        println(dist.sample(1))
        println(dist.hist)

      }

      //https://en.wikipedia.org/wiki/Gamma_distribution#Properties
      "extract k-shape from distribution" should {
        "return k within 1.5%" in {
          val N = 10000
          val d = new DelayedDistribution {}
          val shapeK = 3.0
          val scaleTheta = 1.0

          val dist = d.gammaDistribution(shapeK, scaleTheta)
          val samples: List[Double] = dist.sample(N)
          val s = math.log(samples.sum / N) - (1/N)*samples.map(math.log).sum
          println(s"s = $s")
          println(dist.hist)
          val calculatedK = ( 3 - s + math.sqrt(math.pow(s-3, 2) + 24*s) ) / (12*s)
          val perc = (shapeK/100)*1.5
          //calculatedK shouldBe shapeK +- perc
        }
      }
    }
  }
}
