package com.oktafone.messages

import akka.actor.ActorRef

/**
  * Created by pk on 3/6/16.
  */
object Messages {
  trait MessageType
  case class ChatMessage(msg: String) extends MessageType
  case class FromWebSocketMessage(msg: String) extends MessageType
  case class OutActorRef(actor: ActorRef) extends MessageType
  case class UserLeft(name: String) extends MessageType
}
