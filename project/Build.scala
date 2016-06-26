import sbt.Keys._
import sbt._

object Build extends sbt.Build {  
  val pico_event                = "org.pico"          %%  "pico-event"                % "0.2.0"
  val kafka_clients_0_9         = "org.apache.kafka"  %   "kafka-clients"             % "0.9.0.1"
  val kafka_clients_0_10        = "org.apache.kafka"  %   "kafka-clients"             % "0.10.0.0"
  val kafka_server              = "org.apache.kafka"  %%  "kafka"                     % "0.10.0.0"
  val log4j                     = "log4j"             %   "log4j"                     % "1.2.17"

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

    def it = self.configs(IntegrationTest).settings(Defaults.itSettings: _*)

    def itLibs(modules: ModuleID*) = self.libs(modules.map(_ % "it"): _*)
  }

  lazy val `pico-fake` = Project(id = "pico-fake", base = file("pico-fake"))
      .standard("Fake project").notPublished
      .testLibs(specs2_core)

  lazy val `pico-event-kafka` = Project(id = "pico-event-kafka", base = file("pico-event-kafka"))
      .standard("pico-event shim library for kafka")
      .libs(pico_event)
      .testLibs(specs2_core)

  lazy val `pico-event-kafka-v_0_9` = Project(id = "pico-event-kafka-v_0_9", base = file("pico-event-kafka-v_0_9"))
      .standard("pico-event shim library for kafka")
      .libs(pico_event, kafka_clients_0_9)
      .dependsOn(`pico-event-kafka`)
      .testLibs(specs2_core)

  lazy val `pico-event-kafka-v_0_10` = Project(id = "pico-event-kafka-v_0_10", base = file("pico-event-kafka-v_0_10"))
      .standard("pico-event shim library for kafka")
      .libs(pico_event, kafka_clients_0_10)
      .dependsOn(`pico-event-kafka`)
      .testLibs(specs2_core)
      .it.itLibs(kafka_server, log4j)

  lazy val all = Project(id = "pico-event-kafka-project", base = file("."))
      .notPublished
      .aggregate(`pico-event-kafka`, `pico-event-kafka-v_0_10`, `pico-event-kafka-v_0_9`, `pico-fake`)
}
