import java.io.File

import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}
import com.typesafe.config.{ConfigException, ConfigFactory}

sealed trait SilentActorProtocol
case class SilentMessage(value: String) extends SilentActorProtocol
case class GetState(receiver: ActorRef) extends SilentActorProtocol
case class Ticket(seat: Int)            extends SilentActorProtocol
case class Game(name: String, tickets: Seq[Ticket])
case class Greeting(user: String)       extends SilentActorProtocol

class EchoActor extends Actor {

    override def preStart(): Unit = {
        super.preStart()
        println(Console.YELLOW + "test ovverride hook" + Console.WHITE)
    }
    override def receive: Receive = {
        case msg => sender ! msg
    }

}

class DBWatcher(actorToWatch : ActorRef)
  extends Actor with ActorLogging {
    context.watch(actorToWatch)
    override def receive: Receive = {
        case Terminated(actorRef) => println("actorRef is terminated : ", actorRef)
    }
}

class Greeter(listener: Option[ActorRef] = None)
  extends Actor with ActorLogging {
    override def receive: Receive = {
        case Greeting(who) =>
        val message = "Hello " + who + "!"
        log.info(message)
        listener.foreach(_ ! message)
    }
}

class Kiosk01(nextKiosk: ActorRef) extends Actor {

    override def preStart(): Unit =  {
        super.preStart()
        println(Console.YELLOW + "preStart go" + Console.WHITE)
    }

    override def postStop(): Unit = {
        super.postStop()
        println(Console.YELLOW + "preStop go" + Console.WHITE)
    }

    override def receive: Receive = {
        case game @ Game(_, tickets) => nextKiosk ! game.copy(tickets = tickets.tail)
    }
}

class SilentActor extends Actor {
    var internalState = Vector[String]()
    override def receive: Receive = {
        case SilentMessage(data) =>
            internalState = internalState :+ data
            println("data receive :", data)
        case GetState(receiver) => receiver ! state
    }
    def state = internalState
}


object Sever extends App {

    val config             = ConfigFactory.load()
    val configuration      = ConfigFactory.parseFile(new File("remote_application.conf"))
    val combined           = config.withFallback(configuration)
    val complete           = ConfigFactory.load("remote_application.conf")
    val value              = complete.getString("myAppl.version")

    println(Console.YELLOW + "Server" + value + Console.WHITE)
}
