package com.castalia.scala_academy

import spray.json.DefaultJsonProtocol

case class Status(uptime: String)

trait Protocol extends DefaultJsonProtocol {
  implicit val statusFormatter = jsonFormat1(Status.apply)
}
