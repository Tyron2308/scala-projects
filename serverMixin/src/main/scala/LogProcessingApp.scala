import java.io.File
import akka.actor.SupervisorStrategy.{Decider, Restart, Resume, Stop}
import akka.actor.{Actor,ActorInitializationException, ActorKilledException,
                  ActorRef, ActorSystem, AllForOneStrategy, OneForOneStrategy,
                  PoisonPill, Props, SupervisorStrategy, Terminated}
import scala.io.Source


object LogProcessingProtocol {
  //represent a new file log
  case class LogFile(file: File)
  case class Line(time: Long, message: String, messageType: String)
}



class DbWriter(connector : DBConnector) extends Actor {
  import LogProcessingProtocol._
  override def receive: Receive = {
    case Line(time, message, messageType) =>
      connector.write(Map('time -> time, 'message -> message,
        'messageType -> messageType))
  }
}

class DbSupervisor(writerProps : Props) extends Actor {
  override def supervisorStrategy: SupervisorStrategy = {
    OneForOneStrategy() {
      case _: DbBrokenConnectionException => Restart
    }
  }

  val writer = context.actorOf(writerProps)

  override def receive: Receive = {
    case m => writer forward(m)
  }

}

class LogProcessor(dbSupervisor: ActorRef) extends Actor {
  import LogProcessingProtocol._

  override def receive: Receive = {
    case LogFile(file) =>
      val lines = Source.fromFile(file)
      lines.foreach(dbSupervisor ! _)
  }
}

class LogProcSupervisor(dbSupervisorProps: Props) extends Actor {
  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case _: CorruptedFileException => Resume
  }
  val dbSupervisor = context.actorOf(dbSupervisorProps)
  val logProcProps = Props(new LogProcessor(dbSupervisor))
  val logProcessor = context.actorOf(logProcProps)

  override def receive: Receive = {
    case m => logProcessor forward m
  }


}

class FileWatcher(sourceUri: String, logProcSupervisor: ActorRef)
extends Actor with FileWatchingAbilities {
  register(sourceUri)
  import FileWatcherProtocol._
  import LogProcessingProtocol._

  override def receive: Receive = {
    case NewFile(file, time) => logProcSupervisor ! LogFile(file)
    case SourceAbandoned( path ) =>
      if (path == sourceUri) self ! PoisonPill
  }
}


class FileWatchingSupervisor(sources: Vector[String],
                             logProcSupervisor: Props) extends Actor {
  var fileWatchers: Vector[ActorRef] = sources.map {
    source =>
      val logP = context.actorOf(logProcSupervisor)
      val fileWatcher       = context.actorOf(Props(new FileWatcher(source,
        logP)))

      context.watch(fileWatcher)
      fileWatcher
  }

  override def supervisorStrategy: SupervisorStrategy = AllForOneStrategy(){
    case _: DiskError => Stop
  }

  override def receive: Receive = {
    case Terminated(fileWatcher) =>
      fileWatchers = fileWatchers.filterNot(w => w == fileWatcher)
      if (fileWatchers.isEmpty) self ! PoisonPill
  }
}

object FileWatcherProtocol {
  case class NewFile(file: File, timeAdded: Long)
  case class SourceAbandoned(uri: String)
}
trait FileWatchingAbilities {
  def register(uri: String): Unit = {

  }
}


object LogProcessingApp {
  val source            = Vector("file:///source1/", "file:///source2/")
  val system            = ActorSystem("logprocessing")
  val con               = CassandraConnectionUri.DbConnector
  val writerProps       = Props(classOf[DbWriter], con)
  val dbSuperProps      = Props(classOf[DbSupervisor], writerProps)
  val logProcSuperProps = Props(classOf[LogProcSupervisor], dbSuperProps)
  val topLovelProps     = Props(classOf[FileWatchingSupervisor], source, logProcSuperProps)


  system.actorOf(topLovelProps)

  final val defaultStrategy: SupervisorStrategy = {
    def defaulDecider: Decider = {
      case _: ActorInitializationException => Stop
      case _: ActorKilledException => Stop
      case _: Exception => Restart
    }
    OneForOneStrategy()(defaulDecider)
  }

}
