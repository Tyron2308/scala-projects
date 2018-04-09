name          := "scala-server"
scalaVersion  := "2.11.8"

lazy val set = Seq(
  scalaVersion := "2.11.8",
  scalacOptions ++=  Seq(
    "-unchecked",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:postfixOps",
    "-deprecation",
    "-encoding",
    "utf8"
  )

)
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

lazy val definition = new {
    scalaVersion := "2.11.8"
    val akka_version          = "2.3.7"
    val akka_remote_verison   = "2.3.7"
    val akka24Version         = "2.4.20"
    val akkahttpVersion       = "10.1.0"
    val version_test          = "3.0.5"
    val stm_version           = "0.7"
    val akka_test_version     = "2.5.11"
    val phantomV              = "2.14.5"
    val akka_test             = "com.typesafe.akka" %% "akka-testkit"           % akka_test_version % Test
    val playFramwork          = "com.typesafe.play" %% "play" % "2.6.13"


    val stm                   = "org.scala-stm"     %% "scala-stm"              % stm_version
    val scala_test            = "org.scalactic"     %% "scalactic"              % version_test
    val test                  = "org.scalatest"     %% "scalatest"              % version_test % "test"
    val akka_session          = "com.softwaremill"   % "akka-http-session_2.11" % "0.1.4"
    val akka_http             =  "com.typesafe.akka" % "akka-http_2.11"         % akkahttpVersion
    val akka_core             = "com.typesafe.akka"  % "akka-actor_2.11"        % akka24Version
    val akka_remote           = "com.typesafe.akka"  % "akka-remote_2.11"       % akka24Version
    val phantom_dsl           = "com.outworkers"    %%  "phantom-dsl"          % phantomV
    val phantom_kit           = "com.outworkers"    %%  "phantom-testkit"      % phantomV
    val phantom_connect       = "com.outworkers"    %%  "phantom-connectors"   % phantomV
  }


val phantom           = Seq(definition.phantom_dsl, definition.phantom_connect, definition.playFramwork)
val sequenceAkka      = Seq(definition.akka_core, definition.akka_remote,
                             definition.akka_http, definition.akka_session,
                              definition.stm, definition.akka_test)
lazy val common       = (project in file("common")).settings(set)
lazy val core         = (project in file(".")).settings(set)
                                              .settings(libraryDependencies ++= sequenceAkka ++ phantom,
                                                libraryDependencies += definition.test,
                                                libraryDependencies += definition.scala_test)
                                              .dependsOn(common)

lazy val remoteActor  = (project in file("RemoteActor")).settings(set)
                                                        .settings(libraryDependencies ++= sequenceAkka)
                                                        .dependsOn(common)
