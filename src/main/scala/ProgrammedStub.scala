package castalia
import akka.http.scaladsl.model.StatusCodes._
import castalia.matcher.RequestMatch
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
  def process1(rc : RequestMatch) : Future[StubResponse] = {
    val promise = Promise[StubResponse]
    val p1 = rc.pathParams.find { i => i._1.equals("1") } getOrElse(("1","<nil>"))
    val p2 = rc.pathParams.find { i => i._1.equals("2") } getOrElse(("2","<nil>"))
    val result = ProgrammedStub.Response(s"${p1._2} with ${p2._2}");
    promise.success(new StubResponse(OK.intValue, result.toJson.toString()))
    promise.future
  }
  def process2(rc : RequestMatch) : Future[StubResponse] = {
    val promise = Promise[StubResponse]
    promise.success(new StubResponse(ServiceUnavailable.intValue, s"Not used"))
    promise.future
  }
}