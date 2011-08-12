package edu.cornell.indexbuilder.configurations

import akka.event.EventHandler
import org.slf4j.{Logger,LoggerFactory}
import com.weiglewilczek.slf4s._

/**
 * just testing log levels to see if logback is setup
 */

class LogTestBase {

}


class LogTest extends LogTestBase with Logging {
  def test() : Unit = {
    logger.info( "info message")
    logger.warn( "warning message")
    logger.error( "error message")
    logger.debug( "Debug message")

  }
}

object LogTest  {

  def main(args : Array[String]) : Unit = {
    val lt = new LogTest
    lt.test()
  }
}


