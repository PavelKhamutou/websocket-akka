package com.oktafone.pk


import akka.actor.{ActorLogging, Props, ActorRef, Actor}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{SubscribeAck, Subscribe, Publish}



object UserActor {
  def props(roomId: Int, name: String) = Props(new UserActor(roomId, name))
}

class UserActor(roomId: Int, name: String) extends Actor with ActorLogging {
  import com.oktafone.messages.Messages._

  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(roomId.toString, self)
  log.info(s"User [$name] joined chat room [$roomId]")


  override def receive: Receive = {
    case OutActorRef(actor) =>
      mediator ! Publish(roomId.toString, ChatMessage(s"User [$name] joined chat room [$roomId]"))
      context.become(withOutput(actor))
    case SubscribeAck(Subscribe(id, None, ref)) =>
      log.info(s"Subscription for $id but there is not out actor set.")
    case msg =>
      log.info(s"Got $msg but there is not out actor set. self ! msg")
      self ! msg
  }

  def withOutput(actor: ActorRef): Receive = {
    case FromWebSocketMessage(msg) =>
      log.info(s"Message [$msg] is received from web socket input chanel. This message will be broadcasted")
      val message = s"[$roomId][$name]: $msg"
      mediator ! Publish(roomId.toString, ChatMessage(message))
    case ChatMessage(msg) =>
      log.info(s"Message [$msg] is received from mediator. This message will be redirected to web socket output chanel")
      actor ! msg
    case UserLeft(`name`) =>
      mediator ! Publish(roomId.toString, ChatMessage(s"User [$name] left the channel"))
      log.info(s"User [$name] left the channel")
      context.stop(self)
  }
}
