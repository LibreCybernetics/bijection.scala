ThisBuild / libraryDependencySchemes += "org.scala-native" % "sbt-scala-native" % "always"

// CrossPlatform

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "1.3.2")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.3.2")
addSbtPlugin("org.scala-native"   % "sbt-scala-native"              % "0.4.15")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"                   % "1.13.2")

// Documentation

addSbtPlugin("com.github.sbt" % "sbt-unidoc" % "0.5.0")

// Static Code Analysis

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "3.1.3")

// Testing

addSbtPlugin("org.scoverage"      % "sbt-scoverage" % "2.0.8")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"       % "0.4.5")

// CI/CD

addSbtPlugin("com.github.sbt" % "sbt-ci-release"     % "1.5.12")
addSbtPlugin("com.github.sbt" % "sbt-github-actions" % "0.15.0")
