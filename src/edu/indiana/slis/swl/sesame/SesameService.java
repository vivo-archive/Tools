package edu.indiana.slis.swl.sesame;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.nativerdf.NativeStore;

public class SesameService {

    Repository therepository = null; 
    File dataDir = null;
    // useful -local- constants
    static RDFFormat NTRIPLES = RDFFormat.NTRIPLES;
    static RDFFormat N3 = RDFFormat.N3;
    static RDFFormat RDFXML = RDFFormat.RDFXML;
    static String RDFTYPE =  RDF.TYPE.toString();
    
    
    /**
     *  In memory Sesame repository without inferencing
     */
    public SesameService(String file){
        this(false, file);
    } 
    
    
    
    /**
     * In memory Sesame repository with optional inferencing
     * @param inferencing
     */
    public SesameService(boolean inferencing, String file){
    	dataDir = new File(file);
        try {
            if (inferencing){
            therepository = new SailRepository(new ForwardChainingRDFSInferencer(new NativeStore(dataDir)));
            } else {
                therepository = new SailRepository(new NativeStore(dataDir));
            }
            therepository.initialize();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     *  Literal factory
     * 
     * @param s the literal value
     * @param typeuri uri representing the type (generally xsd)
     * @return
     */
    public org.openrdf.model.Literal Literal(String s, URI typeuri) {
        try {
            RepositoryConnection con = therepository.getConnection();
            try {
                ValueFactory vf = con.getValueFactory();
                if (typeuri == null) {
                    return vf.createLiteral(s);
                } else {
                    return vf.createLiteral(s, typeuri);
                }
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    /**
     * Untyped Literal factory
     * 
     * @param s the literal
     * @return
     */
    public org.openrdf.model.Literal Literal(String s) {
        return Literal(s, null);
    }
    
    
    
    /**
     *  URIref factory
     * 
     * @param uri
     * @return
     */
    public URI URIref(String uri) {
        try {
            RepositoryConnection con = therepository.getConnection();
            try {
                ValueFactory vf = con.getValueFactory();
                return vf.createURI(uri);
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    
    /**
     *  BNode factory
     * 
     * @return
     */
    public BNode bnode() {
        try{
            RepositoryConnection con = therepository.getConnection();
            try {
                ValueFactory vf = con.getValueFactory();
                return vf.createBNode();
            } finally {
                con.close();
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    
    public void addFile(String filepath, String baseURI ,RDFFormat format) {
        try {
            RepositoryConnection con = therepository.getConnection();
            try {
                con.add(new File(filepath), baseURI, format, null);
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    /**
     *  dump RDF graph
     * 
     * @param out output stream for the serialization
     * @param outform the RDF serialization format for the dump
     * @return
     */
    public void dumpRDF(OutputStream out, RDFFormat outform) {
        try {
            RepositoryConnection con = therepository.getConnection();
            try {
                RDFWriter w = Rio.createWriter(outform, out);
                con.export(w, null);
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    /**
     *  Convenience URI import for RDF/XML sources
     * 
     * @param urlstring absolute URI of the data source
     */
    public void addURI(String urlstring) {
        addURI(urlstring, RDFFormat.RDFXML);
    }
    

    /**
     *  Tuple pattern query - find all statements with the pattern, where null 
     *  is a wildcard 
     * 
     * @param s subject (null for wildcard)
     * @param p predicate (null for wildcard)
     * @param o object (null for wildcard)
     * @return serialized graph of results
     */
    public List tuplePattern(URI s, URI p, Value o) {
        try{
            RepositoryConnection con = therepository.getConnection();
            try {
                RepositoryResult repres = con.getStatements(s, p, o, true, null);
                ArrayList reslist = new ArrayList();
                while (repres.hasNext()) {
                    reslist.add(repres.next());
                }
                return reslist;
            } finally {
                con.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    /**
     *  Execute a CONSTRUCT/DESCRIBE SPARQL query against the graph 
     * 
     * @param qs CONSTRUCT or DESCRIBE SPARQL query
     * @param format the serialization format for the returned graph
     * @return serialized graph of results
     */
    public String runSPARQL(String qs, RDFFormat format) {
        try{
            RepositoryConnection con = therepository.getConnection();
            try {
                GraphQuery query = 
                    con.prepareGraphQuery(
                    org.openrdf.query.QueryLanguage.SPARQL, qs);
                StringWriter stringout = new StringWriter();
                RDFWriter w = Rio.createWriter(format, stringout);
                query.evaluate(w);
                return stringout.toString();
            } finally {
                con.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    
    /**
     *  Execute a SELECT SPARQL query against the graph 
     * 
     * @param qs SELECT SPARQL query
     * @return list of solutions, each containing a hashmap of bindings
     */
    
    
    public List runSPARQL(String qs) {
        try{
            RepositoryConnection con = therepository.getConnection();
            try {
                TupleQuery query = 
                    con.prepareTupleQuery(
                    org.openrdf.query.QueryLanguage.SPARQL, qs);
                TupleQueryResult qres = query.evaluate();
                ArrayList reslist = new ArrayList();
                while (qres.hasNext()) {
                    BindingSet b = (BindingSet) qres.next();
                    Set names = b.getBindingNames();
                    HashMap hm = new HashMap();
                    for (Object n : names) {
                        hm.put((String) n, b.getValue((String) n));
                    }
                    reslist.add(hm);
                }
                return reslist;
            } finally {
                con.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     *  Execute a SELECT SPARQL query against the graph 
     * 
     * @param qs SELECT SPARQL query
     * @return list of solutions with single value
     */
    
    public List runColSPARQL(String qs) {
        try{
            RepositoryConnection con = therepository.getConnection();
            try {
                TupleQuery query = 
                    con.prepareTupleQuery(
                    org.openrdf.query.QueryLanguage.SPARQL, qs);
                TupleQueryResult qres = query.evaluate();
                ArrayList reslist = new ArrayList();
                while (qres.hasNext()) {
                    BindingSet b = (BindingSet) qres.next();
                    Set names = b.getBindingNames();
                    for (Object n : names) {
                    	reslist.add(b.getValue((String) n));
                    }
                }
                return reslist;
            } finally {
                con.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    /**
     *  Insert Triple/Statement into graph 
     * 
     * @param s subject uriref
     * @param p predicate uriref
     * @param o value object (URIref or Literal)
     */
    public void add(URI s, URI p, Value o) {
        try {
               RepositoryConnection con = therepository.getConnection();
               try {
                    ValueFactory myFactory = con.getValueFactory();
                    Statement st = myFactory.createStatement((Resource) 
                        s, p, (Value) o);
                    con.add(st);
               } finally {
                  con.close();
               }
            }
            catch (Exception e) {
               // handle exception
            }
    }

    
    
    /**
     *  Import RDF data from a string
     * 
     * @param rdfstring string with RDF data
     * @param format RDF format of the string (used to select parser)
     */
    public void addString(String rdfstring,  RDFFormat format) {
        try {
            RepositoryConnection con = therepository.getConnection();
            try {
                StringReader sr = new StringReader(rdfstring);
                con.add(sr, "", format);
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    
    /**
     *  Import RDF data from a file
     * 
     * @param location of file (/path/file) with RDF data
     * @param format RDF format of the string (used to select parser)
     */
    public void addFile(String filepath,  RDFFormat format) {
        try {
            RepositoryConnection con = therepository.getConnection();
            try {
                con.add(new File(filepath), "", format);
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    

    
    
    
    /**
     *  Import data from URI source
     *  Request is made with proper HTTP ACCEPT header
     *  and will follow redirects for proper LOD source negotiation
     * 
     * @param urlstring absolute URI of the data source
     * @param format RDF format to request/parse from data source
     */
    public void addURI(String urlstring, RDFFormat format) {
        try {
            RepositoryConnection con = therepository.getConnection();
            try {
                URL url = new URL(urlstring);
                URLConnection uricon = (URLConnection) url.openConnection();
                uricon.addRequestProperty("accept", format.getDefaultMIMEType());
                InputStream instream = uricon.getInputStream();
                con.add(instream, urlstring, format);
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}