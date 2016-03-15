package com.oktafone.pk

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.ws.{TextMessage, Message}
import akka.stream.{FlowShape, OverflowStrategy}
import akka.stream.scaladsl._


/**
  * Created by pk on 3/13/16.
  */
object WorkFlow {

  def websocketFlow(id: Int, user: String)(implicit actorSystem: ActorSystem): Flow[Message, Message, _] =
    Flow.fromGraph(GraphDSL.create(Source.actorRef[String](5, OverflowStrategy.fail)) {
      implicit builder => chatSource =>
        import com.oktafone.messages.Messages._
        import GraphDSL.Implicits._

        val fromWebsocket = builder.add(
          Flow[Message].collect {
            case TextMessage.Strict(txt) => FromWebSocketMessage(txt)
          })

        val backToWebsocket = builder.add(
          Flow[String].map {
            case msg => TextMessage(msg)
          })

        val out = builder.materializedValue.map(actor => OutActorRef(actor))

        val merge = builder.add(Merge[MessageType](2))

        val actor = actorSystem.actorOf(UserActor.props(id, user))

        val chatActorSink = Sink.actorRef[MessageType](actor, UserLeft(user))

        out ~> merge.in(0)
        fromWebsocket ~> merge.in(1)

        merge ~> chatActorSink
        chatSource ~> backToWebsocket

        FlowShape(fromWebsocket.in, backToWebsocket.out)
    })
}
