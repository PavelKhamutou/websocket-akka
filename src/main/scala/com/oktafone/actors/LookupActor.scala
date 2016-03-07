package com.oktafone.actors

import scala.concurrent.duration._
import akka.actor._
import akka.actor.Actor.Receive
import com.oktafone.messages.Messages._


/**
  * Created by pk on 3/6/16.
  */
class LookupActor(path: String) extends Actor {

  sendIdentifyRequest()
  def sendIdentifyRequest(): Unit = {
    context.actorSelection(path) ! Identify(path)
    import context.dispatcher
    context.system.scheduler.scheduleOnce(3.seconds, self, ReceiveTimeout)
  }

  override def receive: Receive = {
    case "print" => println(path)
    case ActorIdentity(`path`, Some(actor)) =>
      println("found in lookup")
      context.become(active(actor))
    case ActorIdentity(`path`, None) => println("Not found in loopup")
    case ReceiveTimeout => sendIdentifyRequest()
    case IsFound => sender() ! NotFound
    case _ => println("Not ready yet")
  }

  def active(actor: ActorRef): Receive = {
    case IsFound => sender() ! Found(actor)
    case x => println(s"Something went wrong: $x")
  }

}
