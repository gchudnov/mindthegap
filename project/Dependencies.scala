import sbt._

object Dependencies {

  object versions {
    val scalatest     = "3.2.15"
    val scalacheck    = "1.17.0"
    val scalatestplus = "3.2.15.0"
  }

  private val scalatest     = "org.scalatest"     %% "scalatest"  % versions.scalatest
  private val scalacheck    = "org.scalacheck"    %% "scalacheck" % versions.scalacheck
  private val scalatestplus = "org.scalatestplus" %% "scalacheck-1-17" % versions.scalatestplus

  val Mtg: Seq[ModuleID] = {
    val compile = Seq(
    )
    val test = Seq(
      scalatest,
      scalacheck,
      scalatestplus
    ) map (_ % "test")
    compile ++ test
  }
}