val sharedSettings = Seq(
  name := "direct-styles",
  version := "0.0.1",
  scalaVersion := "3.5.0",
  scalacOptions ++= Seq(
    "-rewrite",
    "-source:3.5-migration"
    // "-source:future"
    // "-language:experimental.modularity"
  ),
  libraryDependencies ++= Seq(
    "ch.epfl.lamp" %%% "gears" % "0.2.0",
    "org.scodec" %%% "scodec-core" % "2.3.0",
    "org.scalameta" %%% "munit" % "1.0.0" % Test
  )
)

val openapiLibraryDependencies = Seq(
  "io.swagger" % "swagger-annotations" % "1.6.5",
  "com.squareup.okhttp3" % "okhttp" % "4.12.0",
  "com.squareup.okhttp3" % "logging-interceptor" % "4.12.0",
  "com.google.code.gson" % "gson" % "2.9.1",
  "org.apache.commons" % "commons-lang3" % "3.12.0",
  "jakarta.ws.rs" % "jakarta.ws.rs-api" % "2.1.6",
  "org.openapitools" % "jackson-databind-nullable" % "0.2.6",
  "io.gsonfire" % "gson-fire" % "1.9.0" % "compile",
  "jakarta.annotation" % "jakarta.annotation-api" % "1.3.5" % "compile",
  "com.google.code.findbugs" % "jsr305" % "3.0.2" % "compile",
  "jakarta.annotation" % "jakarta.annotation-api" % "1.3.5" % "compile",
  "org.junit.jupiter" % "junit-jupiter-api" % "5.10.3" % "test",
  "com.novocode" % "junit-interface" % "0.10" % "test",
  "org.mockito" % "mockito-core" % "3.12.4" % "test"
)

lazy val root = crossProject(JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .settings(sharedSettings)
  .jvmSettings(
    Compile / run / fork := true,
    Compile / run / connectInput := true,
    run / javaOptions ++= Seq(
      "-Xmx1G",
      "-Djava.net.useSystemProxies=false",
      "-DsocksProxyHost=localhost",
      "-DsocksProxyPort=1080",
      "-DsocksProxyVersion=4"
    ),
    libraryDependencies ++= Seq(
      "org.scala-lang" %% "scala3-staging" % scalaVersion.value,
      "com.softwaremill.ox" %% "core" % "0.2.0",
      "com.softwaremill.sttp.client4" %% "core" % "4.0.0-M17",
      "com.softwaremill.sttp.client4" %% "json4s" % "4.0.0-M17",
      "com.softwaremill.sttp.tapir" %% "tapir-netty-server-sync" % "1.11.0",
      "org.json4s" %% "json4s-jackson" % "4.1.0-M6",
      "com.lihaoyi" %% "requests" % "0.9.0",
      "com.lihaoyi" %% "upickle" % "4.0.1",
      "com.googlecode.lanterna" % "lanterna" % "3.1.2",
      "ch.qos.logback" % "logback-classic" % "1.5.6"
    )
  )
