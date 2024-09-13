import sbt.Keys.*
import sbt.*

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
  .settings(Settings.doPublish)
  .settings(
    name := "mtg",
    libraryDependencies ++= Dependencies.Mtg,
  )

lazy val diagram = (project
  .in(file("diagram")))
  .dependsOn(mtg)
  .settings(allSettings)
  .settings(Settings.doPublish)
  .settings(
    name := "mtg-diagram",
    libraryDependencies ++= Dependencies.Mtg,
  )

lazy val diagramAscii = (project
  .in(file("diagram-ascii")))
  .dependsOn(mtg, diagram)
  .settings(allSettings)
  .settings(Settings.doPublish)
  .settings(
    name := "mtg-diagram-ascii",
    libraryDependencies ++= Dependencies.Mtg,
  )

lazy val diagramMermaid = (project
  .in(file("diagram-mermaid")))
  .dependsOn(mtg, diagram)
  .settings(allSettings)
  .settings(Settings.doPublish)
  .settings(
    name := "mtg-diagram-mermaid",
    libraryDependencies ++= Dependencies.Mtg,
  )

lazy val examples = (project
  .in(file("examples")))
  .dependsOn(mtg, diagramAscii, diagramMermaid)
  .settings(Settings.noPublish)
  .settings(
    name := "examples",
    libraryDependencies ++= Dependencies.Examples,
  )

lazy val root = (project
  .in(file(".")))
  .aggregate(mtg, diagram, diagramAscii, diagramMermaid, examples)
  .settings(Settings.noPublish)
  .settings(
    name := "mtg-root"
  )

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("chk", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")
addCommandAlias("plg", "; reload plugins ; libraryDependencies ; reload return")
// NOTE: to use version check for plugins, add to the meta-project (/project/project) sbt-updates.sbt with "sbt-updates" plugin as well.
addCommandAlias("upd", ";dependencyUpdates; reload plugins; dependencyUpdates; reload return")
