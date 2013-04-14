import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "KillerBone"
  val appVersion      = "0.6a"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
		libraryDependencies += "org.apache.commons" % "commons-email" % "1.2",
		libraryDependencies += "com.typesafe" %% "play-plugins-mailer" % "2.1.0"
  )

}
