package castalia.actors

import akka.actor._
import castalia.model.Model._

/**
  * Actor that provides answers based on the json configuration that is used to create this actor
  *
  * Created by Jean-Marc van Leerdam on 2016-01-16
  */
abstract class JsonEndpointActor(myStubConfig: StubConfig) extends Actor with ActorLogging





