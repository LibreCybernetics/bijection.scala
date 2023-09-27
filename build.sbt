import sbt.Keys.publishTo

// Globals

ThisBuild / organization := "dev.librecybernetics"
ThisBuild / licenses     := Seq(
  "LicenseRef-Anti-Capitalist Software License-1.4" -> url("https://anticapitalist.software/"),
  "LicenseRef-Cooperative Softawre License"         -> url("https://lynnesbian.space/csl/formatted/"),
  "Parity-7.0.0"                                    -> url("https://paritylicense.com/versions/7.0.0")
)

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/LibreCybernetics/bijection.scala"),
    "scm:git@github.com:LibreCybernetics/bijection.scala.git"
  )
)

ThisBuild / versionScheme := Some("strict")
ThisBuild / scalaVersion  := Version.scala

val sharedSettings = Seq(
  scalaVersion := Version.scala,
  scalacOptions ++= Seq(
    "-explain",
    "-explain-types",
    // Extra Warnings
    "-deprecation",
    "-feature",
    "-unchecked",
    // Extra flags
    "-language:captureChecking",
    "-language:implicitConversions",
    "-language:saferExceptions",
    "-Ykind-projector:underscores",
    "-Ysafe-init",
    "-Xfatal-warnings"
  ),
  resolvers    := Seq(
    Resolver.mavenLocal,
    "GitHub Package Registry" at "https://maven.pkg.github.com/LibreCybernetics/bijection.scala"
  ),
  publishTo    := Some(
    "GitHub Package Registry" at "https://maven.pkg.github.com/LibreCybernetics/bijection.scala"
  ),
  credentials  := Seq(
    Credentials(
      "GitHub Package Registry",
      "maven.pkg.github.com",
      "LibreCybernetics",
      sys.env.getOrElse("GITHUB_TOKEN", "")
    )
  )
)

ThisBuild / wartremoverErrors ++= Warts.unsafe

val core =
  (projectMatrix in file("core"))
    .jsPlatform(scalaVersions = Seq(Version.scala))
    .jvmPlatform(scalaVersions = Seq(Version.scala))
    .nativePlatform(scalaVersions = Seq(Version.scala))
    .settings(sharedSettings)
    .settings(
      name := "bijection-core",
      libraryDependencies ++= Seq(
        "org.typelevel"     %%% "alleycats-core"     % Version.cats,
        "org.typelevel"     %%% "cats-core"          % Version.cats,
        "org.scalatest"     %%% "scalatest"          % Version.scalatest          % Test,
        "org.scalatest"     %%% "scalatest-wordspec" % Version.scalatest          % Test,
        "org.scalatestplus" %%% "scalacheck-1-17"    % Version.scalatestPlusCheck % Test
      )
    )

val scalacheck =
  (projectMatrix in file("scalacheck"))
    .jsPlatform(scalaVersions = Seq(Version.scala))
    .jvmPlatform(scalaVersions = Seq(Version.scala))
    .nativePlatform(scalaVersions = Seq(Version.scala))
    .dependsOn(core)
    .settings(sharedSettings)
    .settings(
      name := "bijection-scalacheck",
      libraryDependencies ++= Seq(
        "org.scalacheck"    %%% "scalacheck"         % Version.scalacheck,
        "org.scalatest"     %%% "scalatest"          % Version.scalatest          % Test,
        "org.scalatest"     %%% "scalatest-wordspec" % Version.scalatest          % Test,
        "org.scalatestplus" %%% "scalacheck-1-17"    % Version.scalatestPlusCheck % Test
      )
    )

val scalatest =
  (projectMatrix in file("scalatest"))
    .jsPlatform(scalaVersions = Seq(Version.scala))
    .jvmPlatform(scalaVersions = Seq(Version.scala))
    .nativePlatform(scalaVersions = Seq(Version.scala))
    .dependsOn(core)
    .settings(sharedSettings)
    .settings(
      name := "bijection-scalatest",
      libraryDependencies ++= Seq(
        "org.scalatest"     %%% "scalatest"          % Version.scalatest,
        "org.scalatestplus" %%% "scalacheck-1-17"    % Version.scalatestPlusCheck,
        "org.scalatest"     %%% "scalatest-wordspec" % Version.scalatest % Test
      )
    )

val rootMatrix =
  (projectMatrix in file("."))
    .jsPlatform(scalaVersions = Seq(Version.scala))
    .jvmPlatform(scalaVersions = Seq(Version.scala))
    .nativePlatform(scalaVersions = Seq(Version.scala))
    .aggregate(core, scalacheck, scalatest)
    .dependsOn(core)
    .settings(sharedSettings)
    .settings(name := "bijection")
    .enablePlugins(ScalaUnidocPlugin)
    .settings(
      ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(thisProject.value.aggregate*)
    )

// To avoid publishing the default root package / `bijection-scala`

val root = (project in file("fake"))
  .aggregate(rootMatrix.projectRefs*)
  .settings(sharedSettings)
  .settings(publish / skip := true)

// CI/CD

import sbtghactions.GenerativePlugin.autoImport.JavaSpec.Distribution

ThisBuild / githubWorkflowJavaVersions := Seq(
  JavaSpec(Distribution.Zulu, "17"),
  JavaSpec(Distribution.Zulu, "21")
)

ThisBuild / githubWorkflowTargetTags            :=
  Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches :=
  Seq(
    RefPredicate.StartsWith(Ref.Tag("v")),
    RefPredicate.Equals(Ref.Branch("main"))
  )

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    commands = List("ci-release"),
    name = Some("Publish project"),
    env = Map(
      "CI_CLEAN"            -> "; clean",
      "CI_SONATYPE_RELEASE" -> "version", // Not configure, skip
      "GITHUB_TOKEN"        -> "${{ secrets.GITHUB_TOKEN }}",
      "PGP_PASSPHRASE"      -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET"          -> "${{ secrets.PGP_SECRET }}"
    )
  )
)
