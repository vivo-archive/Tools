 import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {

  /* Plugins dependencies are only for plugins! */
  /* Put normal libs in ./project/build/Project.scala */

   val akkaRepo   = "Akka Repo" at "http://akka.io/repository"
   val akkaPlugin = "se.scalablesolutions.akka" % "akka-sbt-plugin" % "1.1.2"
}
