package castalia.model

import castalia.UnitSpecBase
import castalia.model.Model.LatencyConfig

import scala.concurrent.duration.Duration


class ModelSpec extends UnitSpecBase  {

  "A latencyconfig" when {
    "parsing the json" should {
      "find the right distribution" in {
        val constantLatencyConfig = LatencyConfig("constant", Option("100 ms"), None, None)

        constantLatencyConfig.sample() shouldBe Duration("100 ms")

        val gammaLatencyConfig = LatencyConfig("gamma", None, Option("50 ms"), Option("500 ms"))

        assert(gammaLatencyConfig.sample().toMillis >= 0)

        val weibullLatencyConfig = LatencyConfig("weibull", None, Option("6.57803 ms"), Option("7.32456 ms"))

        assert(weibullLatencyConfig.sample().toMillis >= 0)
      }
      "give 0 ms when no distribution is found" in {
        val nonExistingLatencyConfig = LatencyConfig("", None, None, None)
        nonExistingLatencyConfig.sample() shouldBe Duration("0 ms")
      }
    }
    "sampled" should {
      "return unique latencies when not constant" in {
        val gammaLatencyConfig = LatencyConfig("gamma", None, Option("50 ms"), Option("500 ms"))

        val sample1 = gammaLatencyConfig.sample()
        val sample2 = gammaLatencyConfig.sample()
        assert(!sample1.equals(sample2))
      }
    }
  }
}
