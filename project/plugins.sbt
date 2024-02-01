ThisBuild / libraryDependencySchemes += "org.scala-native" % "sbt-scala-native" % "always"

// CrossPlatform

addSbtPlugin("com.eed3si9n"     % "sbt-projectmatrix" % "0.9.2")
addSbtPlugin("org.scala-native" % "sbt-scala-native"  % "0.4.17")
addSbtPlugin("org.scala-js"     % "sbt-scalajs"       % "1.15.0")

// Documentation

addSbtPlugin("com.github.sbt" % "sbt-unidoc" % "0.5.0")

// Static Code Analysis

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "3.1.6")

// Testing

addSbtPlugin("org.scoverage"      % "sbt-scoverage" % "2.0.9")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"       % "0.4.7")

// CI/CD

addSbtPlugin("com.github.sbt" % "sbt-ci-release"     % "1.5.12")
addSbtPlugin("com.github.sbt" % "sbt-github-actions" % "0.22.0")
