import akka.actor.{ActorSystem, Props, UnhandledMessage}
import akka.testkit.{CallingThreadDispatcher, EventFilter, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpec, WordSpecLike}
import akka.pattern._
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}



class Greeter01Test extends TestKit(Greeter01Test.testSystem)
  with WordSpecLike with Matchers with StopSystemAfterAll {
  "The Greeter " must {
    "say Hello World! when Greeting('World') is sent to it " in {
      implicit val timeoute = Timeout(10 seconds)
      val dispactherId = CallingThreadDispatcher.Id
      val props = Props(new Greeter(Some(testActor))).withDispatcher(dispactherId)
      val greeter = system.actorOf(props)
        greeter ! Greeting("World")
        expectMsg("Hello World!")

      }
    }
  "say something else and see what happens" in {
    val props = Props(new Greeter(Some(testActor)))
    val greeter = system.actorOf(props, "greeter02-2")
    system.eventStream.subscribe(testActor, classOf[UnhandledMessage])
    greeter ! "World"
    expectMsg(UnhandledMessage("World", system.deadLetters, greeter))
  }
  "reply with the same message it receive eithout ask " in {
    val echo = system.actorOf(Props[EchoActor], "echo2")
    echo.tell("some message", testActor)
    expectMsg("some message")
  }
}

object Greeter01Test {
  val testSystem = {
    val config = ConfigFactory.parseString(
     """akka.event-handlers = ["akka.testkit.TestEventListener"]""")
    ActorSystem("testsystem", config)
  }


}
