package edu.cornell.indexbuilder

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.shared.Lock
import org.apache.http.HttpResponse

import edu.cornell.mannlib.vitro.indexbuilder.UrisForDataExpansion

import scala.collection.mutable.HashSet
import scala.collection.JavaConversions._
import akka.actor.Actor
import Actor._
import akka.event.EventHandler
import akka.dispatch.Future
import akka.dispatch.Futures


/**
 * Actor that gets RDF for URIs.
 * TODO: what to do about expanding the RDF to handle context nodes?
 * TODO: what to do about external URIs such as WCMC in vivo.cornell.edu?
 */
class RdfLinkedDataWorker (  ) extends Actor {  

  val expand = 
    new UrisForDataExpansion( 
      UrisForDataExpansion.getVivoTwoHopPredicates(), 
      UrisForDataExpansion.getDefaultSkippedPredicates(),
      UrisForDataExpansion.getDefaultSkippedResourceNS()
    )
  
  def receive = {
    // case GetRdf( uri ) =>{
    //   EventHandler.debug(this,"rdf get for URI " + uri )    
      
    //   val master = MasterWorker.getMaster() 
    //   //make a clouse to keep uri and master
    //   val f = ( uri: String, model: Model ) => {    
    //     EventHandler.debug(this,"Got HTTP response for URI " + uri )    
    //     asyncExpandData( uri, model )
    //     master ! GotRdf(uri,model)         
    //   }

    //   HttpWorker.httpWorkRouter ! HttpLinkedDataGet( uri , f )
    // }

    case GetRdf( uri ) => {
      EventHandler.debug(this,"rdf get for URI " + uri )    
      val res = HttpWorker.httpWorkRouter !! HttpLinkedDataGetSync(uri) 
      res match {      
        case Some(model:Model) => {
          val expandedModel = expandData( uri, model )
          MasterWorker.getMaster() ! GotRdf(uri,expandedModel)
        }
        case None =>
          EventHandler.debug(this,"Could not get RDF for URI " + uri )    
      }
    }

  }


  def expandData( uri: String, model: Model): Model = {
    //get list of additional URIs to get linked data for
    val map = expand.getUris( uri, model );    
    val oneHop = asScalaBuffer(map.get("oneHop"))
    val twoHop = asScalaBuffer(map.get("twoHop"))
    
    EventHandler.debug(this,"oneHop expansion list for URI %s: %s".format(uri,oneHop))
    EventHandler.debug(this,"twoHop expansion list for URI %s: %s".format(uri,twoHop))

    val modelExp = singleHopExpansion(oneHop ++ twoHop, model)    
    secondHopExpansion( uri, twoHop, modelExp)
  }

  def singleHopExpansion( oneHop:Seq[String], model:Model):Model = {
    //make a list of yet to be computed results for those URIs
    val listOfFutures: Seq[Future[Model]] = oneHop.map( 
      (uri:String) => HttpWorker.httpWorkRouter !!! HttpLinkedDataGetSync(uri) )
    
    //combine models into one
    val expandedModel = Futures.fold(model)(listOfFutures)(f)
    expandedModel.get  
  }

  def secondHopExpansion( uri:String, twoHop:Seq[String],model:Model):Model = {
    //each of the two hop URIs need to be expanded one more hop
    val urisFor2ndHop = twoHop.flatMap( getSingleHopUris(model) ).distinct
    EventHandler.debug(this,"twoHop 2nd expansion list for URI %s: %s".format(uri,urisFor2ndHop))
    singleHopExpansion( urisFor2ndHop, model)
  }

  def getSingleHopUris(model:Model)(uri:String):Seq[String] = {
    asScalaBuffer( expand.getSingleHopUris(uri,model) )    
  }

  //take to models and add them together
  def f (acc: Model, i: Model) :Model = {
    acc.enterCriticalSection(Lock.WRITE)
    try{
      acc.add(i)
    }finally{
      acc.leaveCriticalSection()
    }
    acc
  }

  def doneExpanding( urisToExpand:HashSet[String], urisExpanded:HashSet[String] ): Boolean = {
    urisExpanded.synchronized{
      return urisExpanded.size == urisToExpand.size
    }      
  }

 
}

