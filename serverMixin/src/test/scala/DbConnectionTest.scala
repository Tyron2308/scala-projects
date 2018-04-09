import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{Matchers, WordSpecLike}

class DbConnectionTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike with Matchers with StopSystemAfterAll {


}
