package edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces;

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

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AddDataPropStmt;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AddObjectPropStmt;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AddResource;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.DropDataPropStmt;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.DropObjectPropStmt;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.DropResource;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.EditDataPropStmt;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.EditObjPropStmt;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.admin.AddNewUser;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.admin.LoadOntology;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.admin.RebuildTextIndex;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.admin.RemoveUser;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.admin.ServerStatus;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.admin.UpdateTextIndex;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.admin.UploadFile;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ontology.CreateOwlClass;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ontology.DefineDataProperty;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ontology.DefineObjectProperty;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ontology.RemoveOwlClass;

/**
 * This is a interface to write a policy that uses the Visitor pattern.
 * In general this should be avoided, just implement PolicyIface.
 * 
 * @author bdc34
 *
 */
public interface VisitingPolicyIface extends PolicyIface {

    //visitor pattern abstract visitor:
    public abstract PolicyDecision visit(IdentifierBundle ids,
            CreateOwlClass action);

    public abstract PolicyDecision visit(IdentifierBundle ids,
            RemoveOwlClass action);

    public abstract PolicyDecision visit(IdentifierBundle ids,
            DefineDataProperty action);

    public abstract PolicyDecision visit(IdentifierBundle ids,
            DefineObjectProperty action);

    public abstract PolicyDecision visit(IdentifierBundle ids,
            AddObjectPropStmt action);

    public abstract PolicyDecision visit(IdentifierBundle ids,
            DropResource action);

    public abstract PolicyDecision visit(IdentifierBundle ids,
            DropDataPropStmt action);

    public abstract PolicyDecision visit(IdentifierBundle ids,
            DropObjectPropStmt action);

    public abstract PolicyDecision visit(IdentifierBundle ids,
            AddResource action);

    public abstract PolicyDecision visit(IdentifierBundle ids,
            AddDataPropStmt action);

    public abstract PolicyDecision visit(IdentifierBundle ids, AddNewUser action);

    public abstract PolicyDecision visit(IdentifierBundle ids, RemoveUser action);

    public abstract PolicyDecision visit(IdentifierBundle ids,
            LoadOntology action);

    public abstract PolicyDecision visit(IdentifierBundle ids,
            RebuildTextIndex action);

    public abstract PolicyDecision visit(IdentifierBundle ids,
            UpdateTextIndex action);

    public abstract PolicyDecision visit(IdentifierBundle ids, UploadFile action);

    public abstract PolicyDecision visit(IdentifierBundle ids,
            ServerStatus action);

    public abstract PolicyDecision visit(IdentifierBundle ids,
            EditDataPropStmt action);

    public abstract PolicyDecision visit(IdentifierBundle ids,
            EditObjPropStmt action);

}