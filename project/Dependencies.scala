import sbt._

object Dependencies {

  object Version {
    val CatsEffect = "1.3.1"
    val Atto       = "0.6.5"
    val Fs2        = "1.0.4" // For cats 1.5.0 and cats-effect 1.2.0
    val Logback    = "1.2.3"
    val ScalaTest  = "3.0.7"
    val ScalaCheck = "1.14.0"
  }

  lazy val Fs2 = "co.fs2" %% "fs2-core" % "1.0.4"

  lazy val CatsEffect = "org.typelevel" %% "cats-effect" % Version.CatsEffect

  lazy val Atto = "org.tpolecat" %% "atto-core" % Version.Atto

  lazy val Logback = "ch.qos.logback" % "logback-classic" % Version.Logback

  lazy val ScalaTest = "org.scalatest" %% "scalatest" % Version.ScalaTest % Test

  lazy val ScalaCheck = "org.scalacheck" %% "scalacheck" % Version.ScalaCheck % Test

}
