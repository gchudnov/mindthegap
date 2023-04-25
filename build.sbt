import sbt.Keys._
import sbt._

Global / cancelable        := true
Global / scalaVersion      := Settings.globalScalaVersion
Global / semanticdbEnabled := true

def testFilter(name: String): Boolean = (name endsWith "Spec")

lazy val testSettings = Seq(
  Test / testOptions ++= Seq(Tests.Filter(testFilter))
)

lazy val allSettings = Settings.shared ++ testSettings

lazy val mtg = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(allSettings)
  .settings(Settings.sonatype)
  .settings(
    name := "mtg",
    libraryDependencies ++= Dependencies.Mtg,
    buildInfoKeys    := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.github.gchudnov.mtg"
  )

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("chk", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")
addCommandAlias("plg", "; reload plugins ; libraryDependencies ; reload return")
// NOTE: to use version check for plugins, add to the meta-project (/project/project) sbt-updates.sbt with "sbt-updates" plugin as well.
addCommandAlias("upd", ";dependencyUpdates; reload plugins; dependencyUpdates; reload return")
