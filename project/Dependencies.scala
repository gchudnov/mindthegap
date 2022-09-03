import sbt._

object Dependencies {

  object versions {
    val scalatest  = "3.2.13"
    val scalacheck = "1.16.0"
  }

  private val scalatest  = "org.scalatest"  %% "scalatest"  % versions.scalatest
  private val scalacheck = "org.scalacheck" %% "scalacheck" % versions.scalacheck

  val Mtg: Seq[ModuleID] = {
    val compile = Seq(
    )
    val test = Seq(
      scalatest,
      scalacheck
    ) map (_ % "test")
    compile ++ test
  }
}