import cats.effect.Effect
import cats.effect.IO
import fs2._
import fs2.StreamApp.ExitCode
import java.nio.file.Paths
import org.http4s.server.blaze.BlazeBuilder
import scala.concurrent.ExecutionContext.Implicits.global
import org.http4s.server.SSLKeyStoreSupport.StoreInfo
import org.http4s.server.{SSLContextSupport, ServerBuilder}
import org.http4s.server.middleware.HSTS

import javax.net.ssl.{SSLContext, KeyManagerFactory}
import java.security.{KeyStore, Security}
import java.security.cert.X509Certificate
import java.io.{FileInputStream, File}

object HelloServerMain extends HelloServer[IO] {
  def builder: BlazeBuilder[IO] = BlazeBuilder[IO]
}

abstract class HelloServer[F[_]: Effect] extends StreamApp[F] {
  def builder: ServerBuilder[F] with SSLContextSupport[F]
  def getContext() : SSLContext = {
    val path = new File("/Users/abhsrivastava/IdeaProjects/bidir-ssl/server/src/main/resources/server.jks")
    println(s"cert path: ${path.getAbsolutePath}")
    println(s"""trustStore: ${System.getProperty("javax.net.ssl.trustStore")}""")
    println(s"""keyStore: ${System.getProperty("javax.net.ssl.keyStore")}""")
    println(s"""trustStore Password: ${System.getProperty("javax.net.ssl.trustStorePassword")}""")
    println(s"""keyStore Password: ${System.getProperty("javax.net.ssl.keyStorePassword")}""")
    
    val ksStream = new FileInputStream(path)
    val ks = KeyStore.getInstance("JKS")
    ks.load(ksStream, "secret".toCharArray)
    ksStream.close()
    val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
    kmf.init(ks, "secret".toCharArray)
    val context = SSLContext.getInstance("TLS")
    context.init(kmf.getKeyManagers, null, null)
    context    
  }

  def stream(args: List[String], requestShutdown: F[Unit]) : fs2.Stream[F, ExitCode] = {
    Scheduler(corePoolSize = 2).flatMap {implicit scheduler => 
      builder
        .withSSLContext(getContext(), clientAuth = true)
        .mountService(HSTS(new HelloService[F].service), "/")
        .bindHttp(8080, "foo.com")
        .serve
    }
  }
}
