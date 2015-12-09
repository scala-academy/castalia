val list = List("this", "maps", "string", "to", "length") map {s => (s, s.length)} toMap


import castalia.AnyJsonObject
import spray.json._
import DefaultJsonProtocol._
val x = """{
            "endpoint": "jsonconfiguredstub",
            "responses": [
              {
                "id": "1",
                "httpStatusCode": 200,
                "response": {
                  "id": "een",
                  "someValue": "123123"
                }
              },
              {
                "id": "2",
                "httpStatusCode": 200,
                "response": {
                  "id": "twee",
                  "someValue": "123123",
                  "someAdditionalValue": "345345"
                }
              },
              {
                "id": "0",
                "httpStatusCode": 404
              }
            ]
          }"""

case class ResponseDef(id: String, httpStatusCode: Int, response: AnyJsonObject)

case class StubDef(endpoint: String, responses: List[ResponseDef])
object StubDefProtocol extends DefaultJsonProtocol {
  implicit val ResponseDefFormat = jsonFormat3(ResponseDef)
  implicit val StubDefFormat = jsonFormat2(StubDef)
}

import StubDefProtocol._
//import ResponseProtocol._
import spray.json._
val json1 =
  StubDef(
    "myEndPoint", List(
      ResponseDef("1", 200, Some(Map("Some" -> JsArray(JsString("11"),JsString("22"),JsString("33"))))),
      ResponseDef("2", 200, Some(Map("Other" -> JsArray(JsString("44"),JsString("55"),JsString("66"))))),
      ResponseDef("3", 200, None))).toJson
//
//val pp1 = json1.prettyPrint
//val rr1 = json1.convertTo[StubDef]
val y = x.parseJson
val test2 = y.convertTo[StubDef]