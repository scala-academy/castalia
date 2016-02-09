package castalia.actors

import akka.actor._
import akka.contrib.pattern.ReceivePipeline

/**
  * Actor that provides answers based on the json configuration that is used to create this actor
  *
  * Created by Jean-Marc van Leerdam on 2016-01-16
  */
trait JsonEndpointActor
  extends Actor with ActorLogging with ReceivePipeline with EndpointRequestInterceptor





