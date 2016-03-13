package com.oktafone

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.oktafone.chat.ChatRooms
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

/**
  * Created by pk on 3/7/16.
  */
object RemoteServer extends App {
  val config = ConfigFactory.load.getConfig("RemoteSystem")
  implicit val actorSystem = ActorSystem("akka-remote-system", config)
  implicit val flowMaterializer = ActorMaterializer()

  val interface = "localhost"
  val port = config.getInt("port")

  import Directives._

  def chat = pathPrefix("chat" / IntNumber) { chatId =>
    parameter('name) { userName =>
      handleWebSocketMessages(ChatRooms.findOrCreate(chatId).websocketFlow(userName))
    }
  }

  def greetings = get {
    pathEndOrSingleSlash {
      complete("Welcome to websocket server")
    }
  }

  def createRoom = path("create" / IntNumber) { chatId =>
    ChatRooms.createRemote(chatId, config.getString("remoteActorPath"))
    complete(s"Welcome to websocket server. Room #$chatId has been created" )
  }


  def chatRemote = pathPrefix("join" / IntNumber) { chatId =>
    parameter('name) { userName =>
//      ChatRooms.createRemote(chatId, config.getString("remoteActorPath"))
      handleWebSocketMessages(ChatRooms.findOrCreate(chatId).websocketFlow(userName))
    }
  }

  val route = greetings ~ chat ~ chatRemote ~ createRoom

  val binding = Http().bindAndHandle(route, interface, port)
  println(s"Server is now online at http://$interface:$port\nPress RETURN to stop...")
  StdIn.readLine()

  import actorSystem.dispatcher

  binding.flatMap(_.unbind()).onComplete(_ => actorSystem.terminate())
  println("Server is down...")
}
