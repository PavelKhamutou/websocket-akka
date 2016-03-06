package com.oktafone.chat.events

import akka.actor.ActorRef
/**
  * Created by pk on 3/6/16.
  */

case class ChatMessage(sender: String, text: String)

object SystemMessage {
  def apply(text: String) = ChatMessage("System", text)
}

sealed trait ChatEvent

case class UserJoined(name: String, userActor: ActorRef) extends ChatEvent

case class UserLeft(name: String) extends ChatEvent

case class IncomingMessage(sender: String, message: String) extends ChatEvent