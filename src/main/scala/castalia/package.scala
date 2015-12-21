import castalia.model.LatencyConfig
import spray.json.JsValue

/**
  * Custom types
  */
package object castalia {
  type AnyJsonObject = Option[Map[String, JsValue]]
  type StatusCode = Int
  type Endpoint = String
  type StubResponse = (Option[LatencyConfig], StatusCode, AnyJsonObject)
  type ResponsesByRequest = collection.mutable.Map[String, StubResponse]
  type StubConfigsByEndpoint = Map[Endpoint, ResponsesByRequest]
}
