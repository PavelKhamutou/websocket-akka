package com.oktafone.actors

import akka.actor.Actor
import akka.actor.Actor.Receive

/**
  * Created by pk on 3/4/16.
  */
class SimpleActor extends Actor {
  override def receive: Receive = {
    case "FROMSYSTEM" => println(s"$self got message from local system.")
    case "FROMREMOTE" =>
      println(s"$self got message from remote system.")
      sender() ! s"I got it! My path is ${self.path}"
  }
}
