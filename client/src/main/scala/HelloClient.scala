import org.http4s.client.blaze.{BlazeClientConfig, Http1Client}
import javax.net.ssl.{SSLContext, KeyManagerFactory}
import java.security.{KeyStore, Security}
import org.http4s._
import cats.effect.IO
import org.http4s.client.dsl.Http4sClientDsl
import java.io.{FileInputStream, File}
import java.security.cert.X509Certificate
import java.nio.file.FileSystems

object HelloClient extends App with Http4sClientDsl[IO] {  
  def getContext() : SSLContext = {
    println(s"""current working directory: ${FileSystems.getDefault.getPath(".").toAbsolutePath}""")
    val path = new File("/Users/abhsrivastava/IdeaProjects/bidir-ssl/client/src/main/resources/client.jks")
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
  val config = BlazeClientConfig.defaultConfig.copy(sslContext = Some(getContext()), checkEndpointIdentification = false)
  val client = Http1Client[IO](config).unsafeRunSync()
  val output = client.expect[String](Uri.uri("https://foo.com:8080/hello")).unsafeRunSync()
  println(output)
}
