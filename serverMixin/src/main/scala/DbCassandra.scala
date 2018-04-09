import java.util.UUID
import javax.inject.Inject
import com.datastax.driver.core.{ConsistencyLevel, ResultSet}
import com.outworkers.phantom.connectors.ContactPoints
import scala.concurrent.Future
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._
import scala.Option
//On mac the file location are :
// Properties : /usr/local/etc/cassandra
// Logs: /usr/local/var/log/cassandra
// Data: /usr/local/var/lib/cassandra/data
import play.api.libs.iteratee.Enumerator

sealed trait ProtocolDatabase
case class LogLine(id: UUID, timeStamp: Long, log: String, emetter: String) extends ProtocolDatabase

object DefaultConnector {
  val hosts     = Seq("127.0.0.1")
  val connector = ContactPoints(hosts).keySpace("whatever")
}

abstract class DbCassandra extends Table[DbCassandra, LogLine] {
  val str : String = "motherfucker"
  object id extends UUIDColumn with PartitionKey
  object timeStamp extends LongColumn
  object log extends StringColumn
  object emmeter extends StringColumn

  def getById(id: UUID): Future[Option[LogLine]] = {
    select.where(_.id eqs id).one()
  }

  def store(emp: LogLine): Future[ResultSet] = {
    insert.value(_.id, emp.id)
      .value(_.timeStamp, emp.timeStamp)
      .value(_.log, emp.log)
      .value(_.emmeter, emp.emetter)
      .consistencyLevel_=(ConsistencyLevel.ALL)
      .future()
  }
}

class MyDatabase(override val connector: CassandraConnection)
  extends Database[MyDatabase](connector) {
  object users extends DbCassandra with Connector
}

object mainer extends App {

  println("main start bb")
  object myDatabase extends MyDatabase(DefaultConnector.connector)
  val t = myDatabase.create()
  val randomed = UUID.randomUUID()
  myDatabase.users.store(new LogLine(randomed, 4, "je vais la baiser", "YOLO"))
  val geted = myDatabase.users.getById(randomed)
  geted onSuccess {
    case (Some(LogLine(id, timestamp, log, emmeter))) =>
      println(Console.YELLOW + "value : " + log + Console.WHITE)
    case (None) => println(Console.RED + "error " + Console.WHITE)
  }

  println(geted)
}
