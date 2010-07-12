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

<%-- Custom short view for ResearchActivity, TeachingActivity, OutreachActivity, and ServiceActivity

    SUBJECT - entity
    PREDICATE - predicateUri
    OBJECT - individual
    
--%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary" %>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://vitro.mannlib.cornell.edu/vitro/tags/StringProcessorTag" prefix="p" %>

<c:set var="vivoCore" value="http://vivoweb.org/ontology/core#" />
<c:set var="rdfs" value="<%= VitroVocabulary.RDFS %>" />
<c:set var="labelUri" value="${rdfs}label" />

<c:set var="researchActivityUri" value="${vivoCore}hasResearchActivity" />
<c:set var="teachingActivityUri" value="${vivoCore}hasTeachingActivity" />
<c:set var="serviceActivityUri" value="${vivoCore}professionalServiceActivity" />
<c:set var="outreachActivityUri" value="${vivoCore}hasOutreachActivity" />

<c:set var="predicateIsActivity" value="${predicateUri == researchActivityUri || 
                                          predicateUri == teachingActivityUri || 
                                          predicateUri == serviceActivityUri ||
                                          predicateUri == outreachActivityUri}" />

<c:choose>
    <c:when test="${!empty individual}"><%-- individual is the OBJECT of the property referenced - in this case, the Activity object --%>
        <c:choose>  
            <c:when test="${!empty predicateUri}">
                
                <c:set var="activity" value="${individual}" />
                <%-- Get the Activity label --%>
                <c:set var="activityLabel" value=" ${activity.name}"/>
                <%-- Create an html link element to the activity --%>
                <c:url var="activityUrl" value="/individual"><c:param name="uri" value="${activity.URI}"/></c:url>
                <c:set var="activityLink" ><a href='${activityUrl}'><p:process>${activityLabel}</p:process></a></c:set>
                <c:if test="${!empty activityLink}" >
                    <c:set var="activityLink" value="<strong>${activityLink}</strong> " />
                </c:if> 

                               
                <%-- Get the role of the person in the activity --%>
                <c:set var="role" value="${activity.dataPropertyMap['http://vivoweb.org/ontology/core#role'].dataPropertyStatements[0].data}"/>
                    
                <%-- Get the time span of the service activity --%>
                <c:set var="startYearMonth" value="${activity.dataPropertyMap['http://vivoweb.org/ontology/core#startYearMonth'].dataPropertyStatements[0].data}"/>
                <c:set var="endYearMonth" value="${activity.dataPropertyMap['http://vivoweb.org/ontology/core#endYearMonth'].dataPropertyStatements[0].data}"/>
                <c:set var="timeSpan" value="" />
                <c:if test="${!empty startYearMonth || !empty endYearMonth}" >
                    <c:if test="${!empty startYearMonth}" >
                        <c:set var="start" value="${fn:split(startYearMonth, '-')}" />
                        <c:set var="startDate" value="${start[1]}/${start[0]}" />  
                        <c:set var="timeSpan" value="${startDate}" />                  
                    </c:if>
                    <c:set var="timeSpan" value="${timeSpan} - " />
                    <c:if test="${!empty endYearMonth}">
                        <c:set var="end" value="${fn:split(endYearMonth, '-')}" />
                        <c:set var="endDate" value="${end[1]}/${end[0]}" /> 
                        <c:set var="timeSpan" value="${timeSpan}${endDate}" />                       
                    </c:if>
                </c:if>

                <%-- Construct the final output --%>   
                <p:process>${activityLink} </p:process> 
                
                <%-- We need a join but we have to keep getting variables in and out of JSTL/EL/Java. Do this for now even though it's ugly. --%>
                <c:choose>                
                    <c:when test="${!empty role && !empty timeSpan}">
                        <p:process>${role}, ${timeSpan}</p:process>
                    </c:when>   
                    <c:when test="${!empty role}">
                        <p:process>${role}</p:process>
                    </c:when>  
                    <c:when test="${!empty timeSpan}">
                        <p:process>${timeSpan}</p:process>
                    </c:when>
                </c:choose>         
            </c:when>
            
            <c:otherwise> <%-- no predicate --%>
                <c:out value="No predicate available for custom rendering ..."/>
            </c:otherwise>
        </c:choose>
    </c:when>
    
    <c:otherwise> <%-- no object --%>
        <c:out value="Got nothing to draw here ..."/>
    </c:otherwise>
</c:choose>
