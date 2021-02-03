// *****************************************************************************
// Projects
// *****************************************************************************

lazy val cookieCutter =
  project
    .in(file("."))
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        library.clients,
        library.kafka,
        library.kafkaAvro,
        library.avro,
        library.avro4s,
        library.airframeLog,
        library.logback,
        library.scalatest % Test,
      ),libraryDependencies ~= { _.map(_.exclude("org.slf4j", "slf4j-log4j12")) }
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val kafka = "2.7.0"
      val cp = "6.0.1"
      val avro = "1.9.2"
      val avro4s = "4.0.4"
      val scalatest = "3.2.0"
      val airframeLog = "20.12.1"
      val logback = "1.2.3"
    }
    val clients = "org.apache.kafka" % "kafka-clients" % Version.kafka
    val kafka = "org.apache.kafka" %% "kafka" % Version.kafka
    val avro = "org.apache.avro" % "avro" % Version.avro
    val kafkaAvro = "io.confluent" % "kafka-avro-serializer" % Version.cp
    val avro4s = "com.sksamuel.avro4s" %% "avro4s-kafka" % Version.avro4s
    val airframeLog = "org.wvlet.airframe" %% "airframe-log" % Version.airframeLog
    val logback = "ch.qos.logback" % "logback-core" % Version.logback
    val scalatest = "org.scalatest" %% "scalatest" % Version.scalatest
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val commonSettings =
  Seq(
    scalaVersion := "2.13.4",
    organization := "example.com",
    organizationName := "example.com",
    startYear := Some(2021),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-encoding", "UTF-8",
      "-Ywarn-unused:imports",
    ),
    resolvers ++= Seq("confluent" at "https://packages.confluent.io/maven",
      Resolver.sonatypeRepo("releases"),
      Resolver.mavenLocal
    ),
    scalafmtOnCompile := true,
)
