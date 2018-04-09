import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest._
import org.scalatest.matchers.Matcher

trait StopSystemAfterAll extends BeforeAndAfterAll {
  this: TestKit with Suite =>
  override protected def afterAll() {
    super.afterAll()
    system.terminate()
  }
}

class test extends TestKit(ActorSystem("testsystem")) with WordSpecLike
  with Matchers with StopSystemAfterAll {
    "A Silent Actor " must {
      "change state when it receive a message" in {
          val silentActor = TestActorRef[SilentActor]
          silentActor ! SilentMessage("whisper")
         // silentActor.underlyingActor.state should contain("Whisper")
      }
      "change state when it receive a message , multi-threaded " in {
        //fail("not implemented yet")
        val silentActor = system.actorOf(Props[SilentActor], "s3")
        silentActor ! SilentMessage("whisper1")
        silentActor ! SilentMessage("whisper2")
        silentActor ! GetState(testActor)

        expectMsg(Vector("whisper1", "whisper2"))
      }
    }
  "A Sending Actor " must {
    "send a message to an actor when it has finished "in {
      val props         = Props(new Kiosk01(testActor))
      val sendingActor  = system.actorOf(props, "kiosk1")
      val tickets       = Vector(Ticket(1), Ticket(2), Ticket(3))
      val game          = Game("Laker vs Bulls ", tickets)

      sendingActor ! game
      expectMsgPF() {
        case Game(_, tickets) => tickets.size should be(game.tickets.size -1)
      }
    }
  }
}