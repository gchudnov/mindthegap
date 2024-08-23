import sbt.Keys._
import sbt._

Global / cancelable        := true
Global / scalaVersion      := Settings.globalScalaVersion
Global / semanticdbEnabled := true

def testFilter(name: String): Boolean = (name.endsWith("Spec"))

lazy val testSettings = Seq(
  Test / testOptions ++= Seq(Tests.Filter(testFilter))
)

lazy val allSettings = Settings.shared ++ testSettings

lazy val mtg = (project
  .in(file("mtg")))
  .settings(allSettings)
  .settings(Settings.publishGithub)
  .settings(
    name := "mtg",
    libraryDependencies ++= Dependencies.Mtg,
  )

lazy val mtgDiagram = (project
  .in(file("mtg-diagram")))
  .dependsOn(mtg)
  .settings(allSettings)
  .settings(Settings.publishGithub)
  .settings(
    name := "mtg-diagram",
    libraryDependencies ++= Dependencies.Mtg,
  )

lazy val mtgAscii = (project
  .in(file("mtg-ascii")))
  .dependsOn(mtg, mtgDiagram)
  .settings(allSettings)
  .settings(Settings.publishGithub)
  .settings(
    name := "mtg-ascii",
    libraryDependencies ++= Dependencies.Mtg,
  )

lazy val mtgMermaid = (project
  .in(file("mtg-mermaid")))
  .dependsOn(mtg, mtgDiagram)
  .settings(allSettings)
  .settings(Settings.publishGithub)
  .settings(
    name := "mtg-mermaid",
    libraryDependencies ++= Dependencies.Mtg,
  )

lazy val examples = (project
  .in(file("examples")))
  .dependsOn(mtg, mtgAscii, mtgMermaid)
  .settings(Settings.noPublish)
  .settings(
    name := "mtg-examples",
    libraryDependencies ++= Dependencies.Examples,
  )

lazy val root = (project
  .in(file(".")))
  .aggregate(mtg, mtgDiagram, mtgAscii, mtgMermaid, examples)
  .settings(Settings.noPublish)
  .settings(
    name := "mtg-root"
  )

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("chk", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")
addCommandAlias("plg", "; reload plugins ; libraryDependencies ; reload return")
// NOTE: to use version check for plugins, add to the meta-project (/project/project) sbt-updates.sbt with "sbt-updates" plugin as well.
addCommandAlias("upd", ";dependencyUpdates; reload plugins; dependencyUpdates; reload return")
