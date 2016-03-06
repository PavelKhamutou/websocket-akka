package com.oktafone

import akka.actor.{Props, ActorSystem}
import akka.util.Timeout
import com.oktafone.actors.SimpleActor
import com.typesafe.config.ConfigFactory

/**
  * Created by pk on 3/4/16.
  */
/*object Actors extends App {
  val config = ConfigFactory.load.getConfig("LocalActor")
  val system = ActorSystem("LocalActor", config)
  val worker = system.actorOf(Props[SimpleActor], "SimpleActor")
  println(s"SimpleActor path is ${worker.path}")
  worker ! "FROMSYSTEM"
}


object RemoteActor extends App {
  val config = ConfigFactory.load.getConfig("RemoteActor")
  val system = ActorSystem("RemoteActor", config)
  val worker = system.actorSelection("akka.tcp://LocalActor@127.0.0.1:2552/user/SimpleActor")
  println(s"SimpleActor path is ${worker.anchorPath}")
  import scala.concurrent.duration._
  import akka.pattern.ask
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val timeout = Timeout(5 seconds)
  val feature = (worker ? "FROMREMOTE").mapTo[String]
  feature.onComplete {
    case x: Any => println(x)
  }
}*/