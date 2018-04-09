import Protocol.{Login, Logout}
import akka.actor.{Actor, ActorRef, Props}
import scala.collection.mutable.HashMap

class User(actor: ActorRef, chatClient: ChatClient) extends Actor {
  override def receive: Receive = ???
}

trait SessionManagementTrait { this: Actor =>

  val storage: ActorRef
  val sessions: HashMap[String, ActorRef] = new HashMap[String, ActorRef]

  protected def sessionManagement: Receive = {
    case Login(user) =>
      val session = this.context.actorOf(Props(new User(storage,
          new ChatClient(user))), name = "encule")
      sessions += (user -> session)

    case Logout(username) =>
      if (sessions.contains(username)) {
        val ref = sessions(username)
        context.stop(ref)
        sessions -= username
      }
  }

  protected def shutDownSession = {
    sessions.foreach {
      _ match {
        case (username, reference) =>
          println(Console.RED + "bye motherfucker" + Console.WHITE)
          context.stop(reference)

      }
    }
  }


}

