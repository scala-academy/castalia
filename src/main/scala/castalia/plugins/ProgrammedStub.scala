package castalia.plugins

import akka.http.scaladsl.model.StatusCodes._
import castalia.matcher.RequestMatch
import castalia.matcher.types.Params
import castalia.model.Model.StubResponse
import spray.json.DefaultJsonProtocol

import scala.concurrent.{Future, Promise}


/**
  * TODO: Place this File in another projectr. This project must have a dependecy to this castalia project
  *
  **/
object ProgrammedStub extends DefaultJsonProtocol{

  case class Response(result: String)

  implicit val responseConfigFormat = jsonFormat1(Response)
}

class ProgrammedStub {
  private def findParamOrDefault(params : Params, name : String, default : String) : (String, String) = {
    findParam(params, name).getOrElse(name, default)
  }
  private def findParam(params : Params, name : String) : Option[(String, String)] = {
    params.find {param => param._1.equals(name)}
  }
  def process1(rc : RequestMatch) : Future[StubResponse] = {
    val promise = Promise[StubResponse]
    val p1 = findParamOrDefault(rc.pathParams, "1", "<nil>")
    val p2 = findParamOrDefault(rc.pathParams, "2", "<nil>")
    val result = ProgrammedStub.Response(s"${p1._2} with ${p2._2}")
    promise.success(new StubResponse(OK.intValue, result.toJson.toString()))
    promise.future
  }
  def process2(rc : RequestMatch) : Future[StubResponse] = {
    val promise = Promise[StubResponse]
    promise.failure(new Exception("some expected failure"))
    promise.future
  }
}