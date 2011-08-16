package edu.cornell.indexbuilder

import akka.actor.Actor
import akka.actor.Actor._
import akka.actor.MaximumNumberOfRestartsWithinTimeRangeReached
import akka.config.Supervision._
import com.weiglewilczek.slf4s.Logging

class Supervisor extends Actor with Logging{
  self.faultHandler = OneForOneStrategy(List(classOf[Throwable]),5,5000)
  
  def receive ={

    case MaximumNumberOfRestartsWithinTimeRangeReached(
      victimActorRef, maxNrOfRetries, withinTimeRange, lastExceptionCausingRestart) =>{
      logger.error("FUCKTHIS SHIT! " + lastExceptionCausingRestart)
    }

  }
}
