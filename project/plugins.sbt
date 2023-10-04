ThisBuild / libraryDependencySchemes += "org.scala-native" % "sbt-scala-native" % "always"

// CrossPlatform

addSbtPlugin("com.eed3si9n"     % "sbt-projectmatrix" % "0.9.1")
addSbtPlugin("org.scala-native" % "sbt-scala-native"  % "0.4.15")
addSbtPlugin("org.scala-js"     % "sbt-scalajs"       % "1.14.0")

// Documentation

addSbtPlugin("com.github.sbt" % "sbt-unidoc" % "0.5.0")

// Static Code Analysis

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "3.1.4")

// Testing

addSbtPlugin("org.scoverage"      % "sbt-scoverage" % "2.0.9")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"       % "0.4.6")

// CI/CD

addSbtPlugin("com.github.sbt" % "sbt-ci-release"     % "1.5.12")
addSbtPlugin("com.github.sbt" % "sbt-github-actions" % "0.16.0")
