package com.oktafone.chat


import akka.actor.{ActorIdentity, Identify, ActorRef, Actor}
import com.oktafone.chat.events._

/**
  * Created by pk on 3/6/16.
  */
class ChatRoomActor(roomId: Int) extends Actor {
  var participants: Map[String, ActorRef] = Map.empty[String, ActorRef]
  println(s"ChatRoomActor ${self.path} has been created")

  override def receive: Receive = {
    case UserJoined(name, actorRef) =>
      participants += name -> actorRef
      broadcast(SystemMessage(s"User $name joined channel..."))
      println(s"User $name joined channel[$roomId]")

    case UserLeft(name) =>
      println(s"User $name left channel[$roomId]")
      broadcast(SystemMessage(s"User $name left channel[$roomId]"))
      participants -= name

    case msg: IncomingMessage =>
      println(s"ChatRoomActor: ${self}")
      broadcast(ChatMessage(msg.sender, msg.message))
  }

  def broadcast(message: ChatMessage): Unit = participants.values.foreach(_ ! message)
}
