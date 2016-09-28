name in ThisBuild := "SHACL"

scalaVersion in ThisBuild := "2.11.8"

scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation", "-unchecked")

enablePlugins(GitVersioning)

val ivyLocal = Resolver.file("localIvy", file(Path.userHome.absolutePath + "/.ivy2/local"))(Resolver.ivyStylePatterns)

resolvers += ivyLocal

import ReleaseTransformations._

// see https://github.com/sbt/sbt-release#the-default-release-process
releaseProcess := Seq[ReleaseStep](
  inquireVersions,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  setNextVersion,
  commitNextVersion
)

lazy val coreDependencies = Seq(
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0-M7" % "test",
  libraryDependencies += "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2.1",
  libraryDependencies += "org.jsoup" % "jsoup" % "1.9.2",
  libraryDependencies += "org.typelevel" %% "cats" % "0.7.2",
  libraryDependencies += "org.eclipse.rdf4j" % "rdf4j-model" % "2.0.1",
  libraryDependencies += "org.eclipse.rdf4j" % "rdf4j-rio-turtle" % "2.0.1",
  libraryDependencies += "com.github.jsonld-java" % "jsonld-java" % "0.8.3",
  libraryDependencies += "com.typesafe" % "config" % "1.3.1"
)

lazy val root = project
  .in(file("."))
  .aggregate(core, web)

lazy val core = project
  .in(file("core"))
  .settings(coreDependencies)

val circeVersion = "0.4.1"

lazy val webDependencies = Seq(
  libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % "2.4.10",
  libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit"  % "2.4.10" % "test",
  libraryDependencies += "io.circe" %% "circe-core" % circeVersion,
  libraryDependencies += "io.circe" %% "circe-generic" % circeVersion,
  libraryDependencies += "io.circe" %% "circe-parser" % circeVersion,
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0-M7" % "test"
)


lazy val web = project
  .in(file("web"))
  .settings(webDependencies)
  .dependsOn(core)
