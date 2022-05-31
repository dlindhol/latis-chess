ThisBuild / organization := "lasp"
ThisBuild / scalaVersion := "2.13.8"

val latisVersion = "a6d6504"

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "ch.qos.logback"                % "logback-classic"            % "1.2.8" % Runtime,
    "com.splunk.logging"            % "splunk-library-javalogging" % "1.10.0" % Runtime,
    "org.codehaus.groovy"           % "groovy-all"                 % "3.0.8" % Runtime,
    "org.typelevel"                %% "log4cats-slf4j"             % "2.1.1",
    "org.scalatest"                %% "scalatest"                  % "3.2.10" % Test,
    "com.github.latis-data.latis3" %% "latis3-core"                % latisVersion,
    "com.github.latis-data.latis3" %% "latis3-netcdf"              % latisVersion,
    "com.github.latis-data.latis3" %% "latis3-server"              % latisVersion,
    "com.github.latis-data.latis3" %% "latis3-test-utils"          % latisVersion % Test
  ),
  resolvers ++= Seq(
    "Splunk Releases" at "https://splunk.jfrog.io/splunk/ext-releases-local",
    "Unidata" at "https://artifacts.unidata.ucar.edu/content/repositories/unidata-releases",
    "jitpack" at "https://jitpack.io"
  ),
  scalacOptions -= "-Xfatal-warnings"
)

lazy val testDatasets = taskKey[Unit]("Run dataset tests")
testDatasets := {
  (datasets / Test / runMain).toTask(" latis.util.DatasetTester").value
}

lazy val buildInfoSettings = Seq(
  buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
  buildInfoPackage := "latis.util"
)

lazy val dockerSettings = Seq(
  docker / imageNames := {
    val tag = if (isSnapshot.value) "dev" else version.value
    Seq(ImageName(s"${name.value}:$tag"))
  },
  docker / dockerfile := {
    val mainclass = "latis.server.Latis3ChessServer"
    val dataFiles = file("datasets/data")
    val fdmlFiles = file("datasets/fdml")
    val fdml = "-Dlatis.fdml.dir=\"/fdml\""
    val depClasspath = (Runtime / managedClasspath).value
    val intClasspath = (Runtime / internalDependencyAsJars).value
    val cp = (depClasspath ++ intClasspath).files.map { x =>
      s"/app/${x.getName}"
    }.mkString(":")
    
    val entryCommand = s"exec java $$JAVA_OPTS $fdml -cp $cp $mainclass"

    new Dockerfile {
      from("eclipse-temurin:17-jre-alpine")
      expose(8080)
      entryPoint("/bin/sh", "-c", entryCommand)
      copy(depClasspath.files, "/app/")
      copy(intClasspath.files, "/app/")
      copy(dataFiles, "/data")
      copy(fdmlFiles, "/fdml")
    }
  }
)

//=== Sub-projects ============================================================

lazy val server = project
  .enablePlugins(DockerPlugin)
  .enablePlugins(BuildInfoPlugin)
  .settings(commonSettings)
  .settings(dockerSettings)
  .settings(buildInfoSettings)
  .settings(
    name := "latis3-chess-server"
  )

lazy val util = project
  .settings(commonSettings)
  .settings(
    name := "latis3-chess-util"
  )

lazy val datasets = project
  .settings(commonSettings)
  .settings(
    name := "latis3-chess-datasets"
  )
