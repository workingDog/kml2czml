
name := "kml2czml"

version := (version in ThisBuild).value

organization := "com.github.workingDog"

scalaVersion := "2.11.11"

crossScalaVersions := Seq("2.11.11", "2.12.2")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.5.14",
  "com.github.workingDog" %% "scalaczml" % "0.5",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
  "com.github.workingDog" %% "scalakml" % "1.3")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalacOptions ++= Seq( "-unchecked", "-deprecation",  "-feature"  )

homepage := Some(url("https://github.com/workingDog/kml2czml"))

licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

mainClass in(Compile, run) := Some("com.kodekutters.czml.Kml2Czml")

mainClass in assembly := Some("com.kodekutters.czml.Kml2Czml")

assemblyJarName in assembly := "kml2czml_2.11-1.0.jar"
