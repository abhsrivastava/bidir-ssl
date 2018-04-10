import cats.effect.Effect
import cats.effect.IO
import fs2._
import fs2.StreamApp.ExitCode
import org.http4s.server.blaze.BlazeBuilder
import scala.concurrent.ExecutionContext.Implicits.global

object HelloServerMain extends HelloServer[IO]
class HelloServer[F[_]: Effect] extends StreamApp[F] {
  def stream(args: List[String], requestShutdown: F[Unit]) : fs2.Stream[F, ExitCode] = {
    Scheduler(corePoolSize = 2).flatMap {implicit schduler => 
      BlazeBuilder[F]
        .bindHttp(8080)
        .mountService(new HelloService[F].service, "/")
        .serve
    }
  }
}