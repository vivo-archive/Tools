

package main.scala
import com.hp.hpl.jena.rdf.model.ModelFactory
import edu.cornell.indexbuilder.SelectorGeneratorForVivo
import edu.cornell.indexbuilder.SolrDocWorker
import java.io.Reader
import java.io.StringReader
 
import org.scalatest.matchers.MustMatchers
import org.scalatest.Spec
import scala.util.parsing.json.JSON._
import akka.actor.Actor._

class RdfToDocumentSpec extends Spec with MustMatchers{

  describe("Process of converting RDF to a SolrDocument") {

    val testRdf1Str = """
<http://vivo.med.cornell.edu/individual/cwid-ems2001>
a       <http://xmlns.com/foaf/0.1/Person> , <http://xmlns.com/foaf/0.1/Agent> , <http://www.w3.org/2002/07/owl#Thing> ;
<http://www.w3.org/2000/01/rdf-schema#label> "Shepard, Elizabeth M" . """

    it("Can create a SolrDocument from simple RDF"){
      val selGen = new SelectorGeneratorForVivo( "unused" ) 
      val model = ModelFactory.createDefaultModel()
      model.read( (new StringReader( testRdf1Str)).asInstanceOf[Reader], null, "N3")
      val doc = SolrDocWorker.individualToDocument( "siteUrl!", "http://vivo.med.cornell.edu/individual/cwid-ems2001", model, selGen )
      assert( doc != null )      
    }
  }
}
