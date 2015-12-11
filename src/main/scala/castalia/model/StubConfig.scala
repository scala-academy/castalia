package castalia.model

import castalia.{StatusCode, AnyJsonObject}

/**
  * Created by M07H817 on 11-12-2015.
  */

case class ResponseConfig(id:String, httpStatusCode:StatusCode, response:AnyJsonObject)
case class StubConfig(endpoint: String, responses: List[ResponseConfig])
