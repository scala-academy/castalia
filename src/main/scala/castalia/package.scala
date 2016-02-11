import spray.json.JsValue

package object castalia {
  type AnyJsonObject = Option[Map[String, JsValue]]
  type StatusCode = Int
  type Endpoint = String
  type EndpointIds = Option[Map[String, String]]
  type Metrics = Map[String, Int]
}
