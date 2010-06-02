/*
Copyright (c) 2010, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package edu.cornell.mannlib.vitro.webapp.dao.jena;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.util.iterator.ClosableIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.cornell.mannlib.vitro.webapp.beans.User;
import edu.cornell.mannlib.vitro.webapp.dao.UserDao;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

public class UserDaoJena extends JenaBaseDao implements UserDao {

    private static final String ROLE_PROTOCOL = "role:/";

    public UserDaoJena(WebappDaoFactoryJena wadf) {
        super(wadf);
    }

    @Override
    protected OntModel getOntModel() {
    	return getOntModelSelector().getUserAccountsModel();
    }
    
    
    
    public List<User> getAllUsers() {
        List<User> allUsersList = new ArrayList<User>();
        getOntModel().enterCriticalSection(Lock.READ);
        try {
            ClosableIterator userStmtIt = getOntModel().listStatements(null, RDF.type, USER);
            try {
                while (userStmtIt.hasNext()) {
                 Statement stmt = (Statement) userStmtIt.next();
                 OntResource subjRes = (OntResource) stmt.getSubject().as(OntResource.class);
                 allUsersList.add(userFromUserInd(subjRes));
                }
            } finally {
                userStmtIt.close();
            }
        } finally {
            getOntModel().leaveCriticalSection();
        }
        return allUsersList;
    }

    public User getUserByURI(String URI) {
        getOntModel().enterCriticalSection(Lock.READ);
        try {
            return userFromUserInd(getOntModel().getOntResource(URI));
        } finally {
            getOntModel().leaveCriticalSection();
        }
    }

    public String insertUser(User user) {
    	return insertUser(user,getOntModel());
    }

    public String insertUser(User user, OntModel ontModel) {
        String userURI = null;
        ontModel.enterCriticalSection(Lock.WRITE);
        try {
            userURI = (user.getURI()==null) ? DEFAULT_NAMESPACE+user.getUsername().replaceAll("\\W","") : user.getURI();
            com.hp.hpl.jena.ontology.Individual test = ontModel.getIndividual(userURI);
            int count = 0;
            while (test != null) {
                ++count;
                userURI+="_"+count;
                test = ontModel.getIndividual(userURI);
            }
            com.hp.hpl.jena.ontology.Individual userInd = ontModel.createIndividual(userURI, ontModel.getResource(USER.getURI()));
            addPropertyStringValue(userInd, ontModel.getProperty(VitroVocabulary.USER_USERNAME), user.getUsername(), ontModel);
            addPropertyStringValue(userInd, ontModel.getProperty(VitroVocabulary.USER_FIRSTNAME), user.getFirstName(), ontModel);
            addPropertyStringValue(userInd, ontModel.getProperty(VitroVocabulary.USER_LASTNAME), user.getLastName(), ontModel);
            addPropertyStringValue(userInd, ontModel.getProperty(VitroVocabulary.USER_MD5PASSWORD), user.getMd5password(), ontModel);
            addPropertyStringValue(userInd, ontModel.getProperty(VitroVocabulary.USER_ROLE), user.getRoleURI(), ontModel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ontModel.leaveCriticalSection();
        }
        user.setURI(userURI);
        return userURI;
    }

    public void deleteUser(User user) {
    	deleteUser(user,getOntModel());
    }

    public void deleteUser(User user, OntModel ontModel) {
        ontModel.enterCriticalSection(Lock.WRITE);
        try {
            OntResource userRes = ontModel.getOntResource(user.getURI());
            if (userRes != null) {
                userRes.remove();
            }
        } finally {
            ontModel.leaveCriticalSection();
        }
    }

    public User getUserByUsername(String username) {
        getOntModel().enterCriticalSection(Lock.READ);
        try {    
	        Property usernameProp = getOntModel().getProperty(VitroVocabulary.USER_USERNAME);    
            Iterator stmtIt = getOntModel().listStatements(null, usernameProp, getOntModel().createTypedLiteral(username));
            if (stmtIt.hasNext()) {
                Statement stmt = (Statement) stmtIt.next();
                Individual userInd = getOntModel().getIndividual(stmt.getSubject().getURI());
                return userFromUserInd(userInd);
            } else {
            	stmtIt = getOntModel().listStatements(null, usernameProp, getOntModel().createLiteral(username));
            	if (stmtIt.hasNext()) {
                    Statement stmt = (Statement) stmtIt.next();
                    Individual userInd = getOntModel().getIndividual(stmt.getSubject().getURI());
                    return userFromUserInd(userInd);
            	} else {
            		return null;
            	}
            }
        } finally {
            getOntModel().leaveCriticalSection();
        }
    }

    private User userFromUserInd(OntResource userInd) {
        User user = new User();
        user.setURI(userInd.getURI());
        user.setNamespace(userInd.getNameSpace());
        user.setLocalName(userInd.getLocalName());
        try {
            user.setUsername(((Literal)userInd.getProperty(getOntModel().getProperty(VitroVocabulary.USER_USERNAME)).getObject()).getString());
        } catch (Exception e) {}
        try {
            user.setMd5password(((Literal)userInd.getProperty(getOntModel().getProperty(VitroVocabulary.USER_MD5PASSWORD)).getObject()).getString());
        } catch (Exception e) {}
        try {
            user.setOldPassword(((Literal)userInd.getProperty(getOntModel().getProperty(VitroVocabulary.USER_OLDPASSWORD)).getObject()).getString());
        } catch (Exception e) {}
        try {
            user.setLoginCount(getPropertyNonNegativeIntValue(userInd,ResourceFactory.createProperty(VitroVocabulary.USER_LOGINCOUNT)));
            if (user.getLoginCount()<0) {
            	user.setLoginCount(0);
            }
        } catch (Exception e) {e.printStackTrace();}
        try {
            user.setRoleURI(((Literal)userInd.getProperty(getOntModel().getProperty(VitroVocabulary.USER_ROLE)).getObject()).getString().substring(6));
        } catch (Exception e) {log.error("Unable to set user role\n");e.printStackTrace(); user.setRoleURI("1");}  // TODO: fix this
        try {
            user.setLastName(((Literal)userInd.getProperty(getOntModel().getProperty(VitroVocabulary.USER_LASTNAME)).getObject()).getString());
        } catch (Exception e) {}
        try {
            user.setFirstName(((Literal)userInd.getProperty(getOntModel().getProperty(VitroVocabulary.USER_FIRSTNAME)).getObject()).getString());
        } catch (Exception e) {}
        try {
            user.setFirstTime(getPropertyDateTimeValue(userInd, getOntModel().getProperty(VitroVocabulary.vitroURI+"firstTime")));
        } catch (Exception e) {}
        return user;
    }

    public void updateUser(User user) {
    	updateUser(user,getOntModel());
    }

    public void updateUser(User user, OntModel ontModel) {
       ontModel.enterCriticalSection(Lock.WRITE);
        try {
            OntResource userRes = ontModel.getOntResource(user.getURI());
            if (userRes != null) {
                updatePropertyStringValue(userRes, ontModel.getProperty(VitroVocabulary.USER_USERNAME), user.getUsername(), ontModel);
                updatePropertyStringValue(userRes, ontModel.getProperty(VitroVocabulary.USER_FIRSTNAME), user.getFirstName(), ontModel);
                updatePropertyStringValue(userRes, ontModel.getProperty(VitroVocabulary.USER_LASTNAME), user.getLastName(), ontModel);
                if (user.getRoleURI() != null && user.getRoleURI().indexOf(ROLE_PROTOCOL) != 0) {
                    user.setRoleURI(ROLE_PROTOCOL+user.getRoleURI());
                }
                updatePropertyStringValue(userRes, ontModel.getProperty(VitroVocabulary.USER_ROLE), user.getRoleURI(), ontModel);
                updatePropertyStringValue(userRes, ontModel.getProperty(VitroVocabulary.USER_MD5PASSWORD), user.getMd5password(), ontModel);
                updatePropertyStringValue(userRes, ontModel.getProperty(VitroVocabulary.USER_OLDPASSWORD), user.getOldPassword(), ontModel);
                updatePropertyDateTimeValue(userRes, ontModel.getProperty(VitroVocabulary.USER_FIRSTTIME), user.getFirstTime(), ontModel);
                updatePropertyNonNegativeIntValue(userRes, ResourceFactory.createProperty(VitroVocabulary.USER_LOGINCOUNT), user.getLoginCount(), ontModel);
            } else {
                log.error("DEBUG UserDaoJena - "+user.getURI()+" not found");
            }
        } finally {
            ontModel.leaveCriticalSection();
        }
    }

    public List<String> getIndividualsUserMayEditAs(String userURI) {
        List<String> uris = new ArrayList<String>();
        OntModel ontModel = getOntModel();
        ontModel.enterCriticalSection(Lock.READ);
        try{
             StmtIterator it = ontModel.listStatements(
                    ontModel.createResource(userURI),
                    ontModel.getProperty(VitroVocabulary.MAY_EDIT_AS),
                    (RDFNode)null);
            while(it.hasNext()){
                try{
                    Statement stmt = (Statement) it.next();
                    if( stmt != null && stmt.getObject()!= null 
                            && stmt.getObject().asNode() != null 
                            && stmt.getObject().asNode().getURI() != null )
                        uris.add(stmt.getObject().asNode().getURI());
                }catch(Exception ex){
                    log.debug("error in getIndividualsUserMayEditAs()",ex);
                }
            }
        }finally{
            ontModel.leaveCriticalSection();
        }
        return uris;
    }

    //Method to get all user accounts that are associated with a person where said person has email address
    public List<String> getUserAccountEmails() {
        List<String> email = new ArrayList<String>();
        List<String> uris = new ArrayList<String>();
        OntModel ontModel = getOntModel();
        OntModel baseModel =  getOntModelSelector().getFullModel();
        ontModel.enterCriticalSection(Lock.READ);
        String swrcOntology = "http://swrc.ontoware.org/ontology#";
        String emailProperty = swrcOntology + "email";
        String emailValue, uri;
        try{
        	 Property emailProp = ontModel.getProperty(emailProperty);
             StmtIterator it = ontModel.listStatements(
                    null,
                    ontModel.getProperty(VitroVocabulary.MAY_EDIT_AS),
                    (RDFNode)null);
            while(it.hasNext()){
                try{
                    Statement stmt = (Statement) it.next();
                    if( stmt != null && stmt.getObject()!= null 
                            && stmt.getObject().asNode() != null 
                            && stmt.getObject().asNode().getURI() != null )
                    {
                    	
                    uri = stmt.getObject().asNode().getURI();	
                    StmtIterator emailIt = baseModel.listStatements(baseModel.createResource(uri), baseModel.createProperty(emailProperty), (RDFNode) null);
                    while(emailIt.hasNext()) {
                    	Statement emailSt = (Statement) emailIt.next();
                    	if(emailSt != null && emailSt.getObject().isLiteral() && emailSt.getObject() != null) {
                    		email.add(emailSt.getLiteral().getString());
                    		//Issue: this prints out the email in a tags
                    	} else {
                    		//System.out.println("Unfortunately email statement is null");
                    	}
                    }
                       
                    }
                }catch(Exception ex){
                    log.debug("error in get User Account Emails()",ex);
                }
                
                
            } 
            
        }finally{
            ontModel.leaveCriticalSection();
        }
        return email;
    }
    
    //for a specific user account, get the email address
    public String getUserEmailAddress(String userURI) {
    	 OntModel ontModel = getOntModel();
         OntModel baseModel =  getOntModelSelector().getFullModel();
         ontModel.enterCriticalSection(Lock.READ);
         String swrcOntology = "http://swrc.ontoware.org/ontology#";
         String emailProperty = swrcOntology + "email";
         String personUri, emailValue = "";
         
         try {
        	 //Get person account associated with this email address
        	 StmtIterator it = ontModel.listStatements(
                     ontModel.createResource(userURI),
                     ontModel.getProperty(VitroVocabulary.MAY_EDIT_AS),
                     (RDFNode)null);
        	 try{
	        	 while(it.hasNext()) {
	        		 Statement personStmt = (Statement) it.next();
	        		 if(personStmt != null 
	        		 	&& personStmt.getObject() != null 
	        		 	&& personStmt.getObject().asNode() != null
	        		 	&& personStmt.getObject().asNode().getURI() != null) {
	        			 personUri = personStmt.getObject().asNode().getURI();
	        			 
	        			 StmtIterator emailIt = baseModel.listStatements(baseModel.createResource(personUri),
	        					 baseModel.createProperty(emailProperty),
	        					 (RDFNode)null);
	        			 while(emailIt.hasNext()) {
	        				 Statement emailStmt = (Statement) emailIt.next();
	        				 if(emailStmt != null && emailStmt.getObject().isLiteral() && emailStmt.getObject() != null) {
	                     		emailValue = emailStmt.getLiteral().getString();
	                     	}
	        			 }
	        		 }
	        	 }
        	 } catch(Exception ex) {
        		 System.out.println("Error occurred in retrieving email and/or user uri");
        	 }
         }finally{
             ontModel.leaveCriticalSection();
         }
         return emailValue;
    }
    

}
