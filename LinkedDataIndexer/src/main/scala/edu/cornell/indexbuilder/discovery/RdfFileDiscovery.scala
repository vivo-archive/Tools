package edu.cornell.indexbuilder.discovery

import akka.actor.Actor._
import akka.actor.Actor
import akka.actor.ActorRef
import akka.routing.Routing._
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.RDF
import com.weiglewilczek.slf4s.Logging
import edu.cornell.indexbuilder.http.{HttpGetAndProcess, HttpWorker}
import edu.cornell.indexbuilder.MasterWorker
import edu.cornell.mannlib.vitro.indexbuilder.ParseDataServiceJson._
import edu.cornell.mannlib.vitro.indexbuilder.CatalystPageToURIs
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary
import java.io.InputStream
import org.apache.http.client.ResponseHandler
import org.apache.http.util.EntityUtils
import org.apache.http.HttpResponse
import scala.collection.Iterable
import scala.collection.JavaConversions._

/**
 * This is a URI discvoery worker to load all the
 * subject in rdf:type statements in an RDF/XML file.
 */
class RdfFileDiscovery ( in:InputStream )
extends Actor with Logging {
  
  def receive = {

    case DiscoverUrisForSite( siteBaseUrl ) => {
      logger.info("GetUrlsToIndexForSite " + siteBaseUrl)
      self.channel ! URIsDiscovered( siteBaseUrl, readUris( in ))
    }
  
    case e => 
         logger.error("got a mystery message " + e)
  }

  def readUris( in:InputStream ):Iterable[String] = {
    val model = ModelFactory.createDefaultModel()
    try{
      model.read(in,null)
      new ModelIterator( model )      
    } catch {
      case e => {
        logger.error("Could not read model: " + e)
        Nil //return empty list on error
      }
    }
  }

}

class ModelIterator( model:Model) extends Iterable[String]{
  def iterator:Iterator[String] = {    
    val resIter = model.listResourcesWithProperty( model.createProperty(  "http://www.w3.org/1999/02/22-rdf-syntax-ns#type" ), null)

    new  Iterator[String]{
      def hasNext():Boolean={
        resIter.hasNext()
      }      

      def next() : String={
        if( resIter.hasNext() ){
          val res = resIter.nextResource()
          if( res.isURIResource( ))
            return res.getURI()
          else
            "non URI resource" //return a bogus resource for blanks nodes etc.
        }else{
          throw new IllegalAccessError("Attempted to get from itererator when hasNext returns false")
        }
      }
    }
  
  }
}
