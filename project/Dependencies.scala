import sbt._

object Dependencies {

  object versions {
    val scalatest          = "3.2.13"
  }

  private val scalatest     = "org.scalatest"       %% "scalatest"      % versions.scalatest

  val Mtg: Seq[ModuleID] = {
    val compile = Seq(
    )
    val test = Seq(
      scalatest
    ) map (_ % "test")
    compile ++ test
  }
}