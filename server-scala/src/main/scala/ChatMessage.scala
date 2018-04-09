import Protocol.{ChatMessage, GetChatLog}
import akka.actor.{Actor, ActorRef}

import scala.collection.mutable

trait ChatMessage { this: Actor =>
  val sessions: mutable.HashMap[String, ActorRef] // needs someone to provide the Session map

  protected def chatManagement: Receive = {
    case msg @ ChatMessage(from, _) => getSession(from).foreach(_ ! msg)
    case msg @ GetChatLog(from) =>     getSession(from).foreach(_ forward msg)
  }

  private def getSession(from: String) : Option[ActorRef] = {
    if (sessions.contains(from))
      Some(sessions(from))
    else {
      println("user not recognize")
      None
    }
  }


}
