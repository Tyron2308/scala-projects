import akka.actor.{Actor, ActorRef, OneForOneStrategy}
import scala.concurrent.duration.Duration

object ChatServerMixin {
  implicit val timeout: Duration = Duration("1.2 ?s")
}

trait ChatServerMixin extends Actor  {
  //val faultHandler                 = OneForOneStrategy(10)

  val storage: ActorRef
  println(" depart ")

  def receive: Receive
  protected def chatManagement: Receive
  protected def sessionManagement: Receive
  protected def shutDownSession: Receive

  override def postStop() = {
    context.stop(storage)

  }

}
