package castalia.metrics

import castalia.UnitSpecBase

class MetricsRegistrySpec extends UnitSpecBase {

  "A MetricsCollector" when {
    "call to increment" should {
      "increment metric counter" in {

        var mc = new MetricsRegistry()

        mc = mc.increment("endpoint1", "metric1")
        mc = mc.increment("endpoint1", "metric2")
        mc = mc.increment("endpoint1", "metric1")
        mc = mc.increment("endpoint2", "metric1")

        mc.metricsByEndpoint.get("endpoint1") shouldBe Some(Map("metric1" -> 2, "metric2" -> 1))
        mc.metricsByEndpoint.get("endpoint2") shouldBe Some(Map("metric1" -> 1))
        mc.metricsByEndpoint.get("endpoint3") shouldBe None

        mc.metrics("endpoint1") shouldBe Map("metric1" -> 2, "metric2" -> 1)
        mc.metrics("endpoint3") shouldBe Map()
      }
    }

    "call to reset" should {
      "reset metrics" in {
        var mc = new MetricsRegistry()

        mc = mc.reset("endpoint1", "metric1")
        mc.metricsByEndpoint.get("endpoint1") shouldBe Some(Map("metric1" -> 0))

        mc = mc.increment("endpoint1", "metric1")
        mc = mc.increment("endpoint1", "metric2")

        mc = mc.reset("endpoint1", "metric1")
        mc.metricsByEndpoint.get("endpoint1") shouldBe Some(Map("metric1" -> 0, "metric2" -> 1))
      }

    }
  }
}
