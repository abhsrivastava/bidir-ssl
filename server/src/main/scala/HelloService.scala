
import cats.effect.Effect
import org.http4s.dsl.Http4sDsl
import fs2.Scheduler
import scala.concurrent.ExecutionContext

import org.http4s._
import org.http4s.server._
import org.http4s.headers._

class HelloService[F[_]](implicit F: Effect[F]) extends Http4sDsl[F] {
  def service(implicit scheduler: Scheduler, ec: ExecutionContext = ExecutionContext.global) : HttpService[F] = {
    Router[F](
      "" -> rootService
    )
  }
  def rootService(implicit scheduler: Scheduler, ec: ExecutionContext = ExecutionContext.global) : HttpService[F] = {
    HttpService[F] {
      case GET -> Root / "hello" =>
        Ok(s"hello world")
    }
  }
}