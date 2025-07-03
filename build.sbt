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
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.0-RC9",
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0",
      coverageExcludedPackages := "<empty>;de.htwg.se.memory.view.*;de.htwg.se.memory.Memory;",


    coverageEnabled := true,
    coverageMinimumStmtTotal := 50,
    coverageFailOnMinimum := false,
    coverageHighlighting := true

)