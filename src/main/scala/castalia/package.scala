import castalia.model.ResponseConfig
import spray.json.JsValue

/**
  * Created by m07h817 on 9-12-2015.
  */
package object castalia {
  type AnyJsonObject = Option[Map[String, JsValue]]
  type StatusCode = Int
  type Endpoint = String
  type EndpointIds = Option[Map[String, String]]
}
