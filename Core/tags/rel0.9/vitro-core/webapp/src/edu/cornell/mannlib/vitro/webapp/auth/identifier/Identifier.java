package edu.cornell.mannlib.vitro.webapp.auth.identifier;

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

/** 
 * Indicates who the user is and what roles/groups they belong to.
 * The objects returned by this could be anything.  For example, RoleBacedPolicy
 * looks for RoleBacedPolicy.AuthRole objects.
 *  
 * This is a marker interface to indicate that a object is an identifier,
 * implementations of Identifier may provide any sort of identifying functionality or
 * methods.  
 * 
 * <h3>Justification for a methodless interface</h3>
 * This is better than using Object since having method signatures that have
 * Identifier at least indicates the intent of the parameter, even if it is the 
 * same to the compiler.
 * 
 * Policy objects are expected to examine the IdentiferBundle to find the
 * information needed to make a decision.  There is no set pattern as to
 * what will and will not be a configuration of Identifiers that will create
 * a AUTHORIZED decision.  Reflection, Pattern Matching or something similar 
 * will be needed.  
 * 
 * We have no compile time information about what will structures will map 
 * to which Authorization, let's not pretend that we do.
 */
public interface Identifier {

}
