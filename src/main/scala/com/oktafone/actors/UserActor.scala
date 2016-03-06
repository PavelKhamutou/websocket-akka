package com.oktafone.actors

import akka.actor._


/**
  * Created by pk on 3/2/16.
  */
class UserActor extends Actor {
  var counter = 0
  override def receive: Receive = {
    case msg: String =>
      counter += 1
      println(s"$msg is received")
      println(s"$counter is counter")
  }
}
