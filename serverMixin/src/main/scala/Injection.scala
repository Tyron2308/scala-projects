import com.datastax.driver.core.{ConsistencyLevel, ResultSet, Row, Session}
import javax.inject._

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator
import com.outworkers.phantom.connectors.{CassandraConnection, Connector, ContactPoint, KeySpace}
import com.outworkers.phantom.database.{Database, DatabaseProvider}
import com.outworkers.phantom.{CassandraTable, Table}
import com.outworkers.phantom.keys.PartitionKey

import scala.concurrent.Future

/*
object Defaults {
  val Connector = ContactPoint.local

  //ContactPoint.local.keySpace(KeySpace("my_keyspace")

    //.ifNotExists().`with`(replication eqs SimpleStategy.replication_factor(1)))
}

class CassandraDatabase(override val connector: CassandraConnection) extends Database[CassandraDatabase](connector) {
  object users extends DBLog with Connector
}

object CassandraDatabase extends CassandraDatabase()
trait DbProvider extends DatabaseProvider[CassandraDatabase] {
  override val database = CassandraDatabase
}


abstract class DBLog extends Table[DBLog, LogLine] {
  object id extends UUIDColumn with PartitionKey
  object timeStamp extends LongColumn
  object log extends StringColumn
  object infos extends StringColumn



}
*/

