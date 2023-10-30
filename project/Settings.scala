import sbt.Keys._
import sbt._

import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._
import com.jsuereth.sbtpgp.PgpKeys
import com.jsuereth.sbtpgp.SbtPgp.autoImport.usePgpKeyHex

object Settings {
  private val scalaV = "3.3.1"

  private val sharedScalacOptions = Seq(
    "-deprecation", // emit warning and location for usages of deprecated APIs
    // NOTE: it most of the cases explain provides too many details, making errors unreadable
    //    "-explain",                      // explain errors in more detail
    //    "-explain-types",                // explain type errors in more detail
    "-feature",         // emit warning and location for usages of features that should be imported explicitly
    "-indent",          // allow significant indentation.
    "-new-syntax",      // require scala 3.0 new syntax.
    "-print-lines",     // show source code line numbers.
    "-unchecked",       // enable additional warnings where generated code depends on assumptions
    "-Ykind-projector", // allow `*` as wildcard to be compatible with kind projector
    "-Wunused:all",     // enable -Wunused:imports,privates,locals,implicits,nowarn
    "-Wvalue-discard",  // warn when non-Unit expression results are unused
//    "-Xfatal-warnings",              // fail the compilation if there are any warnings
    "-Xmigration",                   // warn about constructs whose behavior may have changed since version
    "-language:existentials",        // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros", // Allow macro definition (besides implementation and application)
    "-language:higherKinds",         // Allow higher-kinded types
    "-language:implicitConversions", // Allow definition of implicit functions called views
    "-language:postfixOps",          // Enable postfixOps
  )

  val globalScalaVersion: String           = scalaV
  val supportedScalaVersions: List[String] = List(scalaV)

  val sharedResolvers: Vector[MavenRepository] = (Seq(Resolver.mavenLocal) ++ Resolver.sonatypeOssRepos("releases")).toVector

  val shared: Seq[Setting[?]] = Seq(
    scalacOptions      := sharedScalacOptions,
    crossScalaVersions := supportedScalaVersions,
    scalaVersion       := scalaV,
    ThisBuild / turbo  := true,
    resolvers          := Resolver.combineDefaultResolvers(sharedResolvers),
    compileOrder       := CompileOrder.JavaThenScala,
    organization       := "com.github.gchudnov",
    homepage           := Some(url("https://github.com/gchudnov/mindthegap")),
    description        := "Intervals, Relations and Algorithms",
    licenses           := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/gchudnov/mindthegap"),
        "scm:git@github.com:gchudnov/mindthegap.git",
      )
    ),
    developers := List(
      Developer(id = "gchudnov", name = "Grigorii Chudnov", email = "g.chudnov@gmail.com", url = url("https://github.com/gchudnov"))
    ),
  )

  val sonatype: Seq[Setting[?]] = Seq(
    publishMavenStyle             := true,
    Test / publishArtifact        := false,
    publishTo                     := Some("Sonatype Releases".at("https://oss.sonatype.org/service/local/staging/deploy/maven2")),
    releaseCrossBuild             := true,
    releaseIgnoreUntrackedFiles   := true,
    releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommandAndRemaining("+publishSigned"),
      releaseStepCommandAndRemaining("sonatypeBundleRelease"),
      setNextVersion,
      commitNextVersion,
      pushChanges,
    ),
  )

  val noPublish: Seq[Setting[?]] = Seq(
    publishArtifact := false,
    publish         := {},
    publishLocal    := {},
    publish / skip  := true,
  )

}
