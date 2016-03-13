package com.oktafone

/**
  * Created by pk on 3/2/16.
  */

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import akka.actor._

import com.oktafone.chat.{ChatRoomActor, ChatRooms}
import com.typesafe.config.ConfigFactory


import scala.io.StdIn

object LocalServer extends App {
  val config = ConfigFactory.load.getConfig("LocalSystem")
  implicit val actorSystem = ActorSystem("akka-local-system", config)
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

  def chatRemote = pathPrefix("join" / IntNumber) { chatId =>
    parameter('name) { userName =>
      ChatRooms.createRemote(chatId, config.getString("remoteActorPath"))
      handleWebSocketMessages(ChatRooms.findOrCreate(chatId).websocketFlow(userName))
    }
  }

  val route = greetings ~ chat ~ chatRemote

  val binding = Http().bindAndHandle(route, interface, port)
  println(s"Server is now online at http://$interface:$port\nPress RETURN to stop...")
  StdIn.readLine()

  import actorSystem.dispatcher

  binding.flatMap(_.unbind()).onComplete(_ => actorSystem.terminate())
  println("Server is down...")
}



