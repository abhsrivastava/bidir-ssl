import org.http4s.client.blaze.{BlazeClientConfig, Http1Client}
import javax.net.ssl.{SSLContext, KeyManagerFactory}
import java.security.{KeyStore, Security}
import org.http4s._
import cats.effect.IO
import org.http4s.client.dsl.Http4sClientDsl
import java.io.{FileInputStream, File}
import java.security.cert.X509Certificate

object HelloClient extends App with Http4sClientDsl[IO] {  
  def getContext() : SSLContext = {
    val path = new File("client/src/main/resources/client.jks")
    println(s"cert path: ${path.getAbsolutePath}")

    val ksStream = new FileInputStream(path)
    val ks = KeyStore.getInstance("JKS")
    ks.load(ksStream, "secret".toCharArray)
    ksStream.close()
    val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
    kmf.init(ks, "secret".toCharArray)
    val context = SSLContext.getInstance("TLS")
    context.init(kmf.getKeyManagers, CertUtil.trustAllCerts, null)
    context
  }
  val config = BlazeClientConfig.defaultConfig.copy(sslContext = Some(getContext()), checkEndpointIdentification = false)
  val client = Http1Client[IO](config).unsafeRunSync()
  val output = client.expect[String](Uri.uri("https://foo.com:8080/hello")).unsafeRunSync()
  println(output)
}

object CertUtil {
  import javax.net.ssl.{TrustManager, X509TrustManager} 
  val x509TM = new X509TrustManager() {
    def getAcceptedIssuers() : Array[X509Certificate] = {null}
    def checkClientTrusted(certs: Array[X509Certificate], authType: String) = {Unit}
    def checkServerTrusted(certs: Array[X509Certificate], authType: String) = {Unit}
  } 
  val trustAllCerts : Array[TrustManager] = List(x509TM).toArray
}