package com.oktafone.chat

import akka.actor.{Props, ActorSystem}
import com.oktafone.actors.LookupActor
import com.oktafone.messages.Messages._

import scala.concurrent.Await

/**
  * Created by pk on 3/6/16.
  */
object ChatRooms {
  var chatRooms: Map[Int, ChatRoom] = Map.empty[Int, ChatRoom]

  var remote: String = _

  def findOrCreate(number: Int)(implicit actorSystem: ActorSystem): ChatRoom = chatRooms.getOrElse(number, createNewChatRoom(number))

  private def createNewChatRoom(number: Int)(implicit actorSystem: ActorSystem): ChatRoom = {
    val path = s"akka.tcp://akka-system@127.0.0.1:$remote/user/room$number"
    val lookup = actorSystem.actorOf(Props(classOf[LookupActor], path))
    import scala.concurrent.duration._
    import akka.pattern.ask
    import akka.util.Timeout
    import scala.concurrent.ExecutionContext.Implicits.global

    implicit val timeout = Timeout(5 seconds)
    val result = (lookup ? IsFound).mapTo[Result]


    val resultFromFuture = Await.result(result, 5 second)

    resultFromFuture match {
      case Found(actor) =>
        println("found in result")
        chatRooms += number -> ChatRoom(number, actor)
      case NotFound =>
        println("not found un result")
        chatRooms += number -> ChatRoom(number)
    }
    chatRooms(number)
  }


}
