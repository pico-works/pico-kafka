import sbt.Keys._
import sbt._

object Build extends sbt.Build {  
  val pico_event                = "org.pico"          %%  "pico-event"                % "0.0.1-12"
  val kafka_clients_0_9         = "org.apache.kafka"  %   "kafka-clients"             % "0.9.0.1"
  val kafka_clients_0_10        = "org.apache.kafka"  %   "kafka-clients"             % "0.10.0.0"

  val specs2_core               = "org.specs2"        %%  "specs2-core"               % "3.7.2"

  implicit class ProjectOps(self: Project) {
    def standard(theDescription: String) = {
      self
          .settings(scalacOptions in Test ++= Seq("-Yrangepos"))
          .settings(publishTo := Some("Releases" at "s3://dl.john-ky.io/maven/releases"))
          .settings(description := theDescription)
          .settings(isSnapshot := true)
    }

    def notPublished = self.settings(publish := {}).settings(publishArtifact := false)

    def libs(modules: ModuleID*) = self.settings(libraryDependencies ++= modules)

    def testLibs(modules: ModuleID*) = self.libs(modules.map(_ % "test"): _*)
  }

  lazy val `pico-event-shim-kafka_0_9` = Project(id = "pico-event-shim-kafka_0_9", base = file("pico-event-shim-kafka_0_9"))
      .standard("pico-event shim library for kafka")
      .libs(pico_event, kafka_clients_0_9)
      .testLibs(specs2_core)

  lazy val `pico-event-shim-kafka_0_10` = Project(id = "pico-event-shim-kafka_0_10", base = file("pico-event-shim-kafka_0_10"))
      .standard("pico-event shim library for kafka")
      .libs(pico_event, kafka_clients_0_10)
      .testLibs(specs2_core)

  lazy val all = Project(id = "pico-event-shim-kafka_0_10-project", base = file("."))
      .notPublished
      .aggregate(`pico-event-shim-kafka_0_10`, `pico-event-shim-kafka_0_9`)
}
