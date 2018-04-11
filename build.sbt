val http4sVersion = "0.18.7"
val certStorePassword = sys.env.getOrElse("CERT_STORE_PASSWORD", "secret")

val commonSettings = Seq(
  organization := "com.abhi",
  version := "1.0.0",
  scalaVersion := "2.12.5",
  libraryDependencies ++= Seq(
    "org.http4s" %% "http4s-core" % http4sVersion,
     "org.http4s" %% "http4s-circe" % http4sVersion
  )
)

val server = (project in file("server"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-server" % http4sVersion
    ),
    fork in run := true,
    javaOptions in run ++= Seq(
      "-Djavax.net.ssl.trustStore=./src/main/resources/server.jks",
      "-Djavax.net.ssl.keyStore=./src/main/resources/server.jks",
      s"-Djavax.net.ssl.trustStorePassword=$certStorePassword",
      s"-Djavax.net.ssl.keyStorePassword=$certStorePassword"
    )
  )

val client = (project in file("client"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-client" % http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion
    ),
    fork in run := true,
    javaOptions in run ++= Seq(
      "-Djavax.net.ssl.trustStore=./src/main/resources/client.jks",
      "-Djavax.net.ssl.keyStore=./src/main/resources/client.jks",
      s"-Djavax.net.ssl.trustStorePassword=$certStorePassword",
      s"-Djavax.net.ssl.keyStorePassword=$certStorePassword"
    )
  )
