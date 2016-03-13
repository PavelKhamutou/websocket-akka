package com.oktafone.chat

import akka.actor.{ActorRef, Props, ActorSystem}
import akka.http.scaladsl.model.ws.{TextMessage, Message}
import akka.stream.{FlowShape, OverflowStrategy}
import akka.stream.scaladsl._
import com.oktafone.chat.events._

/**
  * Created by pk on 3/6/16.
  */
class ChatRoom(roomId: Int, chatRoomActor: ActorRef) {

//  private[this] val chatRoomActor = actorSystem.actorOf(Props(classOf[ChatRoomActor], roomId), roomId.toString)

  def websocketFlow(user: String): Flow[Message, Message, _] =
    Flow.fromGraph(GraphDSL.create(Source.actorRef[ChatMessage](5, OverflowStrategy.fail)) {
      implicit builder => chatSource =>

        import GraphDSL.Implicits._
        val fromWebsocket = builder.add(
          Flow[Message].collect {
            case TextMessage.Strict(txt) => IncomingMessage(user, txt)
          })

        val backToWebsocket = builder.add(
          Flow[ChatMessage].map {
            case ChatMessage(author, text) => TextMessage(s"[$author]: $text")
          }
        )

        val chatActorSink = Sink.actorRef[ChatEvent](chatRoomActor, UserLeft(user))

        val merge = builder.add(Merge[ChatEvent](2))

        val actorAsSource = builder.materializedValue.map(actor => UserJoined(user, actor))

        fromWebsocket ~> merge.in(0)

        actorAsSource ~> merge.in(1)
        merge ~> chatActorSink
        chatSource ~> backToWebsocket

        FlowShape(fromWebsocket.in, backToWebsocket.out)
    })
}


object ChatRoom {
  def apply(roomId: Int)(implicit actorSystem: ActorSystem) = new ChatRoom(roomId, actorSystem.actorOf(Props(classOf[ChatRoomActor], roomId), roomId.toString))
  def apply(roomId: Int, remoteActorRef: ActorRef) = new ChatRoom(roomId, remoteActorRef)
}
