package com.oktafone.messages

import akka.actor.ActorRef

/**
  * Created by pk on 3/6/16.
  */
object Messages {
  case class LookUp(roomId: Int)
  trait Result
  object NotFound extends Result
  case class Found(actor: ActorRef) extends Result
  object IsFound
}
