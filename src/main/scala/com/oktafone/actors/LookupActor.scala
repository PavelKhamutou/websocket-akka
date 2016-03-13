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
    println(s"LookUpActor: Looking for remote path: $path")
    context.actorSelection(path) ! Identify(path)
    import context.dispatcher
    context.system.scheduler.scheduleOnce(3.seconds, self, ReceiveTimeout)
  }

  def receive = identifying

  def identifying: Actor.Receive = {
    case ActorIdentity(`path`, Some(actor)) =>
      println(s"LookUpActor: Got this actor: \n\t${actor.path}\n\t$actor")
      context.watch(actor)
      context.become(active(actor))
    case ActorIdentity(`path`, None) => println(s"Remote actor not available: $path")
    case ReceiveTimeout              => sendIdentifyRequest()

    case IsFound                     =>
      val originalSender = sender()
      println(s"originalSender: $originalSender")
      self ! Founder(originalSender)
    case Founder(originalSender) =>
      self ! Founder(originalSender)
    case _                           => println("Not ready yet")
  }

  def active(actor: ActorRef): Receive = {
    case Founder(originalSender) =>
      println(s"LookUpActor: Got message ${Founder(originalSender)}")
      originalSender ! Found(actor)
      context.stop(self)
    case IsFound =>
      println(s"LookUpActor: Got message $IsFound")
      sender() ! Found(actor)
      context.stop(self)

    case Terminated(`actor`) =>
      println("Room has been closed")
      sendIdentifyRequest()
      context.become(identifying)
    case ReceiveTimeout =>
    case x => println(s"Something went wrong: $x")
  }

}
