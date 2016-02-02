import com.twitter.finagle
import com.twitter.finagle.http.{Response, Method, Request}
import com.twitter.util.Await

val managerAddress = "localhost:9090"
val clientManager = finagle.Http.newService(managerAddress)
val manageResponsesUrl = "/castalia/manager/endpoints/responses"
val postRequest = Request(Method.Post, manageResponsesUrl)
postRequest.host = managerAddress
postRequest.contentType = "application/json"
postRequest.contentString = "{ \"endpoint\": \"doublepathparam/$1/responsedata/$2\", " +
  "\"response\": {" +
  "\"ids\": {\"1\": \"2\",\"2\": \"id2\"}, " +
  "\"delay\": {\"distribution\": \"constant\",\"mean\": \"100 ms\"}," +
  "\"httpStatusCode\": 200," +
  "\"response\": {\"id\": \"een\",\"someValue\": \"123123\"}" +
  "}" +
  "}"

val postResponse: Response = Await.result(clientManager(postRequest))
