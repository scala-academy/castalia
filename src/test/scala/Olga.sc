import akka.http.impl.util.JavaMapping.HttpRequest
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, RejectionHandler}
import castalia._
import castalia.model.ResponseConfig
import castalia.model.StubConfig
import spray.json.JsString

val stub = StubConfigParser.parseStubConfig("jsonconfiguredstub.json")
val stubsByEndpoints = StubConfigParser.readAndParseStubConfigFiles(List("jsonconfiguredstub.json"))
lazy val dynamicStubRoutes = {
  def createRoute(endpoint: String, responses: ResponsesByRequest): Route = path(endpoint) {
    println(s"endpoint: ${endpoint}")
    post {
      println(s"POST")
      path("responses") {
        entity(as[String]) {
          payload =>
            complete(s"Payload: ${payload}")
        }
      }
    } //~
    //get {
    //  path(Segment) { id =>
    //    val response: Option[StubResponse] = responses.get(id)
    //
    //    response match {
    //      case Some((statusCode: StatusCode, optResponse: AnyJsonObject)) =>
    //        optResponse match {
    //          case Some(content) => complete(statusCode, content.toJson)
    //          case _ => complete(statusCode, "")
    //        }
    //      case _ => complete(501, "Unknown response")
    //    }
    //  }
    //}
  }

   stubsByEndpoints map { case (e, r) => createRoute(e, r) } reduceLeft (_ ~ _)

}
dynamicStubRoutes.apply(HttpRequest(Post, ))

