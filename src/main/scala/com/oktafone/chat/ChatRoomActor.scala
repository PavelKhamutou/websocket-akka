package com.oktafone.chat


import akka.actor.{ActorRef, Actor}
import com.oktafone.chat.events._

/**
  * Created by pk on 3/6/16.
  */
class ChatRoomActor(roomId: Int) extends Actor {
  var participants: Map[String, ActorRef] = Map.empty[String, ActorRef]
  println(self.path)

  override def receive: Receive = {
    case UserJoined(name, actorRef) =>
      participants += name -> actorRef
      broadcast(SystemMessage(s"User $name joined channel..."))
      println(s"User $name joined channel[$roomId]")
      println(s"User path is ${actorRef.path}")

    case UserLeft(name) =>
      println(s"User $name left channel[$roomId]")
      broadcast(SystemMessage(s"User $name left channel[$roomId]"))
      participants -= name

    case msg: IncomingMessage =>
      broadcast(ChatMessage(msg.sender, msg.message))
  }

  def broadcast(message: ChatMessage): Unit = participants.values.foreach(_ ! message)
}
