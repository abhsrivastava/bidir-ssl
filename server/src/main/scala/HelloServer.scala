import cats.effect.Effect
import cats.effect.IO
import fs2._
import fs2.StreamApp.ExitCode
import java.nio.file.Paths
import org.http4s.server.blaze.BlazeBuilder
import scala.concurrent.ExecutionContext.Implicits.global
import org.http4s.server.SSLKeyStoreSupport.StoreInfo
import org.http4s.server.{SSLKeyStoreSupport, ServerBuilder}
import org.http4s.server.middleware.HSTS

object HelloServerMain extends HelloServer[IO] {
  def builder: BlazeBuilder[IO] = BlazeBuilder[IO]
}
abstract class HelloServer[F[_]: Effect] extends StreamApp[F] {
  val keypath = Paths.get("src/main/resources/server.jks").toAbsolutePath.toString
  def builder: ServerBuilder[F] with SSLKeyStoreSupport[F]

  println(s"Server Cert Path: ${keypath}")
  def stream(args: List[String], requestShutdown: F[Unit]) : fs2.Stream[F, ExitCode] = {
    Scheduler(corePoolSize = 2).flatMap {implicit scheduler => 
      builder
        .withSSL(StoreInfo(keypath, "secret"), keyManagerPassword = "secret")
        .mountService(HSTS(new HelloService[F].service), "/")
        .bindHttp(8080)
        .serve
    }
  }
}