import Dependencies._

lazy val root = (project in file("."))
  .settings(
    organization := "com.ssn",
    name := "ships",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.8",
    mainClass in assembly := Some("com.ssn.ships.Main"),
    scalacOptions := Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-Xfatal-warnings",
      "-feature",
      "-language:implicitConversions",
      "-language:existentials",
      "-language:higherKinds",
      "-Ypartial-unification"
    ),
    libraryDependencies ++= Seq(
      CatsEffect,
      Fs2,
      Atto,
      Logback,
      ScalaTest
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
  )
