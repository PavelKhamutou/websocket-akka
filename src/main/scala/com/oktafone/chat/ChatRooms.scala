package com.oktafone.chat


import akka.actor.{ActorRef, Props, ActorSystem}
import com.oktafone.actors.LookupActor
import com.oktafone.messages.Messages._

import scala.concurrent.Await

/**
  * Created by pk on 3/6/16.
  */
object ChatRooms {

  var chatRooms: Map[Int, ChatRoom] = Map.empty[Int, ChatRoom]

  def findOrCreate(number: Int)(implicit actorSystem: ActorSystem): ChatRoom = chatRooms.getOrElse(number, createNewChatRoom(number))

  def createRemote(number: Int, path: String)(implicit actorSystem: ActorSystem): Unit = {
    println(s"ChatRooms: Creating remote room #$number")

    val lookupActor = actorSystem.actorOf(Props(classOf[LookupActor], path + number), "look-up-actor")
    import scala.concurrent.duration._
    import akka.pattern.ask
    import akka.util.Timeout
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val timeout = Timeout(5.seconds)
    /*val actorFut = (lookupActor ? IsFound).mapTo[Result].collect {
      case Found(actor) =>
        actorRef = actor
        actor
    }*/



    val actorFut = (lookupActor ? IsFound).mapTo[Result]
    import scala.util.{Failure, Success}
    actorFut.onComplete {
      case Success(Found(actor)) =>
        println(s"Fuck you $actor")
        chatRooms += number -> ChatRoom(number, actor)
      case Failure(x) => println(s"Failure"); x.printStackTrace()
    }

//    actorFut.foreach(println)
//    val q = actorFut.map(x => x)
//    val result = Await.result(actorFut, 10.seconds)
    println("Done")
  }

  private def createNewChatRoom(number: Int)(implicit actorSystem: ActorSystem): ChatRoom = {
    println(s"ChatRooms: Creating room #$number")
    val room = ChatRoom(number)
    chatRooms += number -> room
    room
  }


}
