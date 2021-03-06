name := "WebSockets"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val akkaHttpVersion = "2.0.3"
  val akkaVersion = "2.4.2"

  Seq(
//    "com.typesafe.akka" %% "akka-stream-experimental" % akkaHttpVersion,
//    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
//    "com.typesafe.akka" %% "akka-http-core-experimental" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
//    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % "2.4.2",
    "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.2"

  )
}

connectInput in run := true
fork in run := true