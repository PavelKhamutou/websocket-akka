package com.oktafone

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import com.typesafe.config._
import com.oktafone.pk.WorkFlow
import akka.http.scaladsl.server.Route

import scala.io.StdIn

/**
  * Created by pk on 3/13/16.
  */
object Boot extends App {


  val (httpPort: Int, tcpPort: Int) = if(args.isEmpty) (8080, 2551) else (args(0).toInt, args(1).toInt)
  val interface = "localhost"
  val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + tcpPort).withFallback(ConfigFactory.load())

  implicit val actorSystem: ActorSystem = ActorSystem("akka-system", config)
  implicit val fm: ActorMaterializer = ActorMaterializer()


  import Directives._

  def greetings: Route = get {
    pathEndOrSingleSlash {
      complete("Welcome to websocket server")
    }
  }

  def room: Route = path(IntNumber / """\w+""".r) {
    case (id, name) => handleWebSocketMessages(WorkFlow.websocketFlow(id, name))
  }

  val route: Route = greetings ~ room


  val binding = Http(actorSystem).bindAndHandle(route, interface, httpPort)
  println(s"Server is now online at http://$interface:$httpPort\nPress RETURN to stop...")

  StdIn.readLine()


  import actorSystem.dispatcher

  binding.flatMap(_.unbind()).onComplete(_ => actorSystem.terminate())
  println("Server is down...")


}
