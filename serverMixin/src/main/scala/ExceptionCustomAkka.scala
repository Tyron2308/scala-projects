
@SerialVersionUID(1L)
class DiskError(msg: Error) extends Error(msg) with Serializable
@SerialVersionUID(1L)
class CorruptedFileException(msg: Error) extends Error(msg) with Serializable
@SerialVersionUID(1L)
class DbBrokenConnectionException(msg: Error) extends Error(msg) with Serializable

