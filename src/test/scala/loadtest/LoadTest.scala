//package loadtest
//
//import io.gatling.core.Predef._
//import io.gatling.http.Predef._
//
//import scala.concurrent.duration._
//import scala.language.postfixOps
//import scalaj.http.Http
//
///**
//  * Created by Jean-Marc van Leerdam on 2016-02-01
//  */
//
//class LoadTest extends Simulation {
//
//  val maxUsers = 10
//  val rampUp = 10
//  val testDuration = 60
//
//  val endPoints = List(
//      """{"endpoint":"perf/endpoint_0", "responses": [{"httpStatusCode": 200, "delay": { "distribution": "constant", "mean": "100 ms"},"response": {"value": "0"}}]}""",
//      """{"endpoint":"perf/endpoint_1", "responses": [{"httpStatusCode": 200, "delay": { "distribution": "constant", "mean": "200 ms"},"response": {"value": "1"}}]}""",
//      """{"endpoint":"perf/endpoint_2", "responses": [{"httpStatusCode": 200, "delay": { "distribution": "constant", "mean": "500 ms"},"response": {"value": "2"}}]}""",
//      """{"endpoint":"perf/endpoint_3", "responses": [{"httpStatusCode": 200, "delay": { "distribution": "constant", "mean": "750 ms"},"response": {"value": "3"}}]}""",
//      """{"endpoint":"perf/endpoint_4", "responses": [{"httpStatusCode": 200, "delay": { "distribution": "constant", "mean": "2 s"},   "response": {"value": "4"}}]}"""
//    )
//
//  // Send the endpoints via the management interface to the stub server
//  before {
//    for (endPoint <- endPoints){
//      Http("http://localhost:9090/castalia/manager/endpoints")
//        .postData(endPoint)
//        .header("content-type", "application/json").asString
//    }
//  }
//
//  // setup the scenario
//  val callAllEndpoints = repeat(endPoints.length, "index"){
//    exec( http("perf/endpoint_${index}").get("/perf/endpoint_${index}")).pause(2500 milli)
//  }
//
//  val scenarioA = scenario("Load Test A").exec(callAllEndpoints)
//
//  // create the connection
//  val httpConf = http.baseURL("http://localhost:9000").acceptHeader("application/json")
//
//  // define the test: inject load and duration, assert results
//  setUp(
//    scenarioA.inject(
//      nothingFor( 1 seconds),
//      rampUsers(maxUsers) over (rampUp seconds),
//      constantUsersPerSec(maxUsers) during(testDuration seconds),
//      nothingFor(5 seconds)
//    )
//  ).protocols(httpConf).
//    assertions(global.successfulRequests.percent.is(100))
//}

