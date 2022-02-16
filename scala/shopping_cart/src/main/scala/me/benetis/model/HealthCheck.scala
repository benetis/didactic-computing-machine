import derevo.derive
import derevo.circe.encoder
import io.circe._
import cats._
import cats.implicits._
import monocle.Iso

trait HealthCheck[F[_]] {
  def status: F[AppStatus]
}

@derive(encoder)
case class RedisStatus(value: Status)
@derive(encoder)
case class PostgresStatus(value: Status)

@derive(encoder)
case class AppStatus(
    redis: RedisStatus,
    postgres: PostgresStatus
)

sealed trait Status
object Status {
  case object Okay extends Status
  case object Unreachable extends Status
  val _Bool: Iso[Status, Boolean] =
    Iso[Status, Boolean] {
      case Okay        => true
      case Unreachable => false
    }(if (_) Okay else Unreachable)
  implicit val jsonEncoder: Encoder[Status] =
    Encoder.forProduct1("status")(_.toString)
}
