import sbt.Keys._
import sbt._

import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._
import com.jsuereth.sbtpgp.PgpKeys
import com.jsuereth.sbtpgp.SbtPgp.autoImport.usePgpKeyHex

object Settings {
  private val scalaV = "3.5.0"

  private val sharedScalacOptions = Seq(
    "-encoding",
    "UTF-8",
    "-deprecation", // emit warning and location for usages of deprecated APIs
    // NOTE: it most of the cases explain provides too many details, making errors unreadable
    // "-explain",                      // explain errors in more detail
    // "-explain-types",                // explain type errors in more detail
    "-feature",     // emit warning and location for usages of features that should be imported explicitly
    "-indent",      // allow significant indentation.
    "-new-syntax",  // require scala 3.0 new syntax.
    "-print-lines", // show source code line numbers.
    "-unchecked",   // enable additional warnings where generated code depends on assumptions
    // "-Xfatal-warnings",              // fail the compilation if there are any warnings
    "-Xmigration", // warn about constructs whose behavior may have changed since version
    // "-Xcheck-macros",
    // "-Xprint-types", // Without this flag, we will not see error messages for exceptions during given-macro expansion!
    // "-Ycheck:all", // also for checking macros
    // "-Ycheck-mods",
    // "-Ydebug-type-error",
    // "-Yshow-print-errors",
    "-Xkind-projector", // allow `*` as wildcard to be compatible with kind projector
    // "-Ykind-projector:underscores",
    "-language:existentials",        // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros", // Allow macro definition (besides implementation and application)
    "-language:higherKinds",         // Allow higher-kinded types
    "-language:implicitConversions", // Allow definition of implicit functions called views
    // "-language:namedTypeArguments",
    // "-language:dynamics",
    "-language:postfixOps", // Enable postfixOps
  )

  val globalScalaVersion: String           = scalaV
  val supportedScalaVersions: List[String] = List(scalaV)

  val sharedResolvers: Vector[MavenRepository] = Seq(
    Resolver.mavenLocal,
    Resolver.jcenterRepo,
    "GitHub Package Registry".at("https://maven.pkg.github.com/gchudnov/_"),
  ).toVector

  val shared: Seq[Setting[?]] = Seq(
    ThisBuild / turbo         := true,
    ThisBuild / usePipelining := true,
    scalacOptions             := sharedScalacOptions,
    crossScalaVersions        := supportedScalaVersions,
    scalaVersion              := scalaV,
    resolvers                 := Resolver.combineDefaultResolvers(sharedResolvers),
    compileOrder              := CompileOrder.JavaThenScala,
    organization              := "com.github.gchudnov",
    homepage                  := Some(url("https://github.com/gchudnov/mindthegap")),
    description               := "Intervals, Relations and Algorithms",
    licenses                  := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
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

  val publishSonatype: Seq[Setting[?]] = Seq(
    publishMavenStyle      := true,
    pomIncludeRepository   := { _ => false },
    Test / publishArtifact := false,
    credentials            := Seq(Credentials(Path.userHome / ".sbt" / ".credentials-sonatype")),
    usePgpKeyHex("8A64557ABEC7965C31A1DF8DE12F2C6DE96AF6D1"),
    publishTo := {
      val nexus = "https://s01.oss.sonatype.org/"
      if (isSnapshot.value) Some("snapshots".at(nexus + "content/repositories/snapshots"))
      else Some("releases".at(nexus + "service/local/staging/deploy/maven2"))
    },
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

  val publishGithub: Seq[Setting[?]] = Seq(
    publishMavenStyle := true,
    // pomIncludeRepository          := { _ => false },
    Test / publishArtifact := false,
    credentials            := Seq(Credentials(Path.userHome / ".sbt" / ".credentials-github")),
    publishTo              := Some("GitHub MindTheGap Package Registry".at("https://maven.pkg.github.com/gchudnov/mindthegap")),
    publishConfiguration   := publishConfiguration.value.withOverwrite(true),
  )

  val noPublish: Seq[Setting[?]] = Seq(
    publishArtifact := false,
    publish         := {},
    publishLocal    := {},
    publish / skip  := true,
  )

}
