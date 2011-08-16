 import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {

  /* Plugins dependencies are only for plugins! */
  /* Put normal libs in ./project/build/Project.scala */

  //One-Jar plugin
  val retronymSnapshotRepo = "retronym's repo" at "http://retronym.github.com/repo/releases"
  val onejarSBT = "com.github.retronym" % "sbt-onejar" % "0.2"

   val akkaRepo   = "Akka Repo" at "http://akka.io/repository"
   val akkaPlugin = "se.scalablesolutions.akka" % "akka-sbt-plugin" % "1.1.3"
}
