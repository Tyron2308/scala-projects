import com.datastax.driver.core._
import com.outworkers.phantom.CassandraTable
import com.outworkers.phantom.connectors.{KeySpace, SessionProvider}
import com.typesafe.config.ConfigFactory
import shapeless.ops.coproduct.Inject
import javax.inject._
import scala.concurrent.Future

object CassandraConnectionUri {
  implicit val DbConnector : DBConnector = new DBConnector {
    override val configuration: Map[Symbol, Any] = configurationConnector("cassandra", "port", "hosts", "keyspace")
    override def write(map: Map[Symbol, Any]): Unit = {

    }
  }

}



trait CassandraProvider extends SessionProvider {

  val config = ConfigFactory.load()
  implicit val space: KeySpace = Connector.keyspace

  val cluster = Connector.cluster
  override implicit lazy val session: Session = Connector.session
}

object Connector {
  val keyspaced          = CassandraConnectionUri.DbConnector.configuration(Symbol("keyspace")).asInstanceOf[String]
  val host               = CassandraConnectionUri.DbConnector.configuration(Symbol("hosts")).asInstanceOf[List[String]]
  val keyspace: KeySpace = KeySpace(keyspaced)
  val cluster            = new Cluster.Builder().addContactPoint(host(1))
                                                .withPort(CassandraConnectionUri.DbConnector.configuration(Symbol("port")).asInstanceOf[Int])
                                                .withQueryOptions(new QueryOptions()).build

  val session: Session   = cluster.connect


}




abstract class DBConnector {

  val configuration: Map[Symbol, Any]
  def configurationConnector(configuration: String*): Map[Symbol, Any] = {
    val cassandraConfig = ConfigFactory.load.getConfig("cassandra")
    val port            = cassandraConfig.getInt("port")
    val hosts           = cassandraConfig.getStringList("hosts")
    val keyspace        = cassandraConfig.getString("keyspace")


    Map('configuration -> cassandraConfig,
        'port -> port,
        'hosts -> hosts,
        'keyspace -> keyspace)
  }

  def write(map: Map[Symbol, Any])


}
