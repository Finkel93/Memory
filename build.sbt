val scala3Version = "3.3.5"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Memory",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
    libraryDependencies += "org.mockito" % "mockito-core" % "4.2.0" % Test,
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.19",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
      libraryDependencies += "net.codingwell" %% "scala-guice" % "7.0.0",
    coverageEnabled := true,
    coverageMinimumStmtTotal := 50,
    coverageFailOnMinimum := false,
    coverageHighlighting := true

)