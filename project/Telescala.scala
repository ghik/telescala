import com.github.ghik.plainsbt.ProjectGroup
import sbt.Keys._
import sbt._
import sbtghactions.GenerativePlugin.autoImport._
import sbtghactions.{JavaSpec, RefPredicate}
import sbtide.Keys.ideBasePackages

object Telescala extends ProjectGroup("telescala") {
  object Versions {
    final val AvsCommons = "2.9.0"
  }

  override def globalSettings: Seq[Def.Setting[_]] = Seq(
    excludeLintKeys ++= Set(
      ideBasePackages,
      projectInfo,
    ),
  )

  override def buildSettings: Seq[Def.Setting[_]] = Seq(
    scalaVersion := "2.13.10",
    organization := "com.github.ghik",
    homepage := Some(url("https://github.com/ghik/telescala")),
    ideBasePackages := Seq("com.github.ghik.telescala"),

    githubWorkflowTargetTags ++= Seq("v*"),
    githubWorkflowJavaVersions := Seq(JavaSpec.temurin("17")),
    githubWorkflowPublishTargetBranches := Seq(RefPredicate.StartsWith(Ref.Tag("v"))),

    githubWorkflowPublish := Seq(WorkflowStep.Sbt(
      List("ci-release"),
      env = Map(
        "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
        "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
        "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
        "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
      )
    )),

    projectInfo := ModuleInfo(
      nameFormal = "Telescala",
      description = "Scala Remote Debug Shell",
      homepage = Some(url("https://github.com/ghik/telescala")),
      startYear = Some(2023),
      licenses = Vector(
        "Apache License, Version 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")
      ),
      organizationName = "ghik",
      organizationHomepage = Some(url("https://github.com/ghik")),
      scmInfo = Some(ScmInfo(
        browseUrl = url("https://github.com/ghik/telescala.git"),
        connection = "scm:git:git@github.com:ghik/telescala.git",
        devConnection = Some("scm:git:git@github.com:ghik/telescala.git")
      )),
      developers = Vector(
        Developer("ghik", "Roman Janusz", "romeqjanoosh@gmail.com", url("https://github.com/ghik"))
      ),
    ),

    Compile / scalacOptions ++= Seq(
      "-encoding", "utf-8",
      "-explaintypes",
      "-feature",
      "-deprecation",
      "-unchecked",
      "-language:implicitConversions",
      "-language:existentials",
      "-language:dynamics",
      "-language:experimental.macros",
      "-language:higherKinds",
      "-Werror",
      "-Xlint:-missing-interpolator,-adapted-args,-unused,_",
    ),
  )

  lazy val root: Project = mkRootProject.settings(
    libraryDependencies ++= Seq(
      "com.avsystem.commons" %% "commons-core" % Versions.AvsCommons,
      compilerPlugin("com.avsystem.commons" %% "commons-core" % Versions.AvsCommons),
    ),
  )

  protected def enumerateSubprojects: Seq[Project] = discoverProjects
}
