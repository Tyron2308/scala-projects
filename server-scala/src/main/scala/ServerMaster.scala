import akka.actor._
import Protocol._
import akka.util.Timeout
import akka.pattern._
import scala.concurrent.duration._

object common {
  implicit val timeout: Timeout = Timeout(20 seconds)
}

class ChatService extends
  ChatServerMixin with
  SessionManagementTrait with
  ChatMessage with
  MemoryChatStorage {
  override def preStart() = {

//    start("localhost", 2552)
 //   remote.register("chat:service", self)
    //Register the actor with the specified service id
  }

  override def receive: Receive = sessionManagement orElse
                                  chatManagement orElse
                                  storageMessage

  //override val storage: ActorRef = context.ac
}


class ChatClient(val name: String)(implicit sys: ActorSystem) {
  val chat                 = sys.actorSelection("akka.tcp://actorSystemName@127.0.0.1:5150/user/actorName")
  def chatLog               = ask(chat, GetChatLog(name))(common.timeout).mapTo[ChatLog]
  def login                 = chat  ! Login(name)
  def logout                = chat  ! Logout(name)
  def post(message: String) = chat  ! ChatMessage(name, name + ": " + message)
  //def chatLog               = (chat ? GetChatLog(name))(common.timeout).as[ChatLog]
    //                                                   .getOrElse(throw new Exception("Couldn't get the chat log from ChatServer"))
}


object maintest extends App {
    /*println("correct")
    val system                    = akka.actor.ActorSystem("system")

    val client1 = new ChatClient("jonas")
    val client2 = new ChatClient("patrik")
    client1.login
    client2.login
    client1.post("Hi there")
    client2.post("Hello")
    client1.post("Hi again")
    client1.logout
    client2.logout
  */

  val applicationVersion =
  val list = List(1, 4, 3, 6)

  list match {
    case (h::t::tail) => println(t)
  }


}
