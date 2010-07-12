<%--
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
--%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://vitro.mannlib.cornell.edu/vitro/tags/StringProcessorTag" prefix="p" %>

<c:choose>
	<c:when test="${!empty individual}"><%-- individual is the OBJECT of the property referenced -- the Authorship individual, not the Person or Publication --%>
        <%-- c:set var="authorRank" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#authorRank'].dataPropertyStatements[0].data}"/ --%>
 		<c:choose>
			<c:when test="${!empty predicateUri}">
 			    <c:choose>
				    <c:when test="${predicateUri == 'http://vivoweb.org/ontology/core#authorInAuthorship'}"><%-- SUBJECT is a Person, so get info from Authorship about related Publication --%>
					    <c:choose>
                            <c:when test="${!empty individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedInformationResource']}"><%-- this Position is linked to an Organization --%>
					            <c:set var="name"    value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedInformationResource'].objectPropertyStatements[0].object.name}"/>
                                <c:set var="moniker" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedInformationResource'].objectPropertyStatements[0].object.moniker}"/>
                                <c:set var="year"    value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedInformationResource'].objectPropertyStatements[0].object.dataPropertyMap['http://vivoweb.org/ontology/core#year'].dataPropertyStatements[0].data}"/>
                                <c:set var="label" value="${moniker} ${year}"/>
                                <c:set var="uri"     value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedInformationResource'].objectPropertyStatements[0].object.URI}"/>
                            </c:when>
 				            <c:otherwise><%-- this Position is not linked to a Publication yet; use Authorship name as a placeholder and add link to the Authorship so user can add more information --%>
 				                <c:choose>
 				                    <c:when test="${!empty individual.name}">
 				                        <c:set var="name" value="${individual.name}"/>
 				                    </c:when>
 				                    <c:otherwise>
                                        <c:set var="name" value="unlabeled authorship"/>
                                    </c:otherwise>
 				                </c:choose>
                                <c:set var="label" value="(no publication linked yet)"/>
                                <c:set var="uri" value="${individual.URI}"/>
				            </c:otherwise>
				        </c:choose>
				    </c:when>
				    <c:when test="${predicateUri == 'http://vivoweb.org/ontology/core#informationResourceInAuthorship'}"><%-- SUBJECT is a Publication, so get info from Authorship about the related Person --%>
				    	<c:choose>
				    		<c:when test="${!empty individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedAuthor']}"><%-- there is a related Person --%>
					    		<c:set var="name"  value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedAuthor'].objectPropertyStatements[0].object.name}"/>
                                <c:set var="label" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedAuthor'].objectPropertyStatements[0].object.moniker}"/>
                                <c:set var="uri"   value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedAuthor'].objectPropertyStatements[0].object.URI}"/>
					    	</c:when>
					    	<c:when test="${!empty individual.dataPropertyMap['http://vivoweb.org/ontology/core#authorNameAsListed'].dataPropertyStatements[0].data}"><%-- only an author name has been specified --%>
                                <c:set var="name" value="<strong>${individual.dataPropertyMap['http://vivoweb.org/ontology/core#authorNameAsListed'].dataPropertyStatements[0].data}</strong>"/>
					    	</c:when>
					    	<c:otherwise><%-- no related Person yet (likely from before custom form available) --%>
                                <c:choose>
                                    <c:when test="${!empty individual.name}"><c:set var="name" value="${individual.name}"/></c:when>
                                    <c:otherwise><c:set var="name" value="unlabeled authorship"/></c:otherwise>
                               </c:choose>
                               <c:set var="label" value="(no author linked yet)"/>
                               <c:set var="uri" value="${individual.URI}"/>
					        </c:otherwise>
					    </c:choose>
					</c:when>
				    <c:otherwise>
				        <c:set var="name" value="unknown predicate"/>
				        <c:set var="label" value="please contact your VIVO support team"/>
				        <c:set var="uri" value="${predicateUri}"/>
				    </c:otherwise>
			    </c:choose>
			    <c:choose>
			    	<c:when test="${!empty uri}">
			            <c:url var="olink" value="/entity"><c:param name="uri" value="${uri}"/></c:url>
		                <a href="<c:out value="${olink}"/>"><p:process>${name}</p:process></a> <p:process>${label}</p:process>
		            </c:when>
		            <c:otherwise>
		                <p:process><strong>${name}</strong> ${label}</p:process> 
		            </c:otherwise>
		        </c:choose>
			</c:when>
			<c:otherwise>
				<c:out value="No predicate available for custom rendering ..."/>
			</c:otherwise>
        </c:choose>
	</c:when>
	<c:otherwise>
		<c:out value="Got nothing to draw here ..."/>
	</c:otherwise>
</c:choose>
