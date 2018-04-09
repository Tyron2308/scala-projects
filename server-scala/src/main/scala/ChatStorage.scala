import Protocol.{ChatLog, ChatMessage, GetChatLog}
import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, OneForOneStrategy, Props, SupervisorStrategy}
import scala.concurrent.duration._
import scala.concurrent.stm._

trait MemoryChatStorageFactory { this: Actor =>
  //val storage = this.self.spawnLink[MemoryChatStorage] // starts and links ChatStorage
}

trait MemoryChatStorage extends Actor {

  def one: SupervisorStrategy = {
    OneForOneStrategy(maxNrOfRetries = 5, withinTimeRange = 10 seconds) {
      case _: ArithmeticException => Resume
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception => Escalate
    }
  }

  private var chatLog = Ref(Array[Array[Byte]]())

   def storageMessage : Receive = {
     case msg @ ChatMessage(from, message) =>
       atomic { implicit tnx =>
         val rec = chatLog()
         rec :+ message.getBytes("UTF-8")
       }

     case GetChatLog(_) =>
       val messageList = atomic { implicit tnx =>
         val toMap = chatLog()
         toMap.map(bytes => new String(bytes).toList)
         sender() ! ChatLog(messageList)
         //self.reply(ChatLog(messageList))
       }
   }

  override def postRestart(reason: Throwable) =
    chatLog = Ref(Array[Array[Byte]]())
}
