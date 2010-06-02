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

<%@ page isThreadSafe = "false" %>
<%@ page import="java.util.*,java.lang.String.*" %>

<% if (request.getAttribute("title") != null) { %>
  <h2><%=request.getAttribute("title")%></h2>
<% } %>

<%
String headerStr = (String)request.getAttribute("header");
if ( headerStr == null || (!headerStr.equalsIgnoreCase("noheader")) ) { %>
<%  } %>
<jsp:useBean id="results" class="java.util.ArrayList" scope="request" />
<jsp:useBean id="loginHandler" class="edu.cornell.mannlib.vedit.beans.LoginFormBean" scope="session"/>
<%
int rows = 0;

String minEditRoleStr = (String)request.getAttribute("min_edit_role");

String firstValue = "null";
Integer columnCount = (Integer)request.getAttribute("columncount");
rows = columnCount.intValue();

String clickSortStr = (String)request.getAttribute("clicksort");

if ( rows > 0  && results.size() > rows ) { // avoid divide by zero error in next statement
    String suppressStr = null;
    int columns = results.size() / rows;
    if ( ( suppressStr = (String)request.getAttribute("suppressquery")) == null ) { // only inserted into request if true %>
        <p><i><b><%= columns - 1 %></b> results were retrieved in <b><%= rows %></b> rows for query "<%=request.getAttribute("querystring")%>".</i></p>
<%  }
    if ( clickSortStr != null && clickSortStr.equals("true")) {
        if ( columns > 2 ) { %>
            <p><i>Click on the row header to sort columns by that row.</i></p>
<%      }
    }  %>
    <table border="0" cellpadding="2" cellspacing="0" width="100%">
<%  String[] resultsArray = new String[results.size()]; // see Core Java Vol. 1 p.216
    results.toArray( resultsArray );
    firstValue = resultsArray[ rows ];
    request.setAttribute("firstvalue",firstValue);
    //secondValue= resultsArray[ rows + 1 ];
    String classString = "";
    boolean[] postQHeaderCols = new boolean[ columns ];
    for ( int eachcol=0; eachcol < columns; eachcol++ ) {
        postQHeaderCols[ eachcol ] = false;
    }
    for ( int thisrow = 0; thisrow < rows; thisrow++ ) {
        //int currentPostQCol = 0;
        boolean dropRow = false;
            for ( int thiscol = 0; thiscol < columns; thiscol++ ) {
            String thisResult= resultsArray[ (rows * thiscol) + thisrow ];
            if ( "+".equals(thisResult) ) { /* occurs all in first row, so postQHeaderCols should be correctly initialized */
                classString = "postheaderright";
                postQHeaderCols[ thiscol ] = true;
                //++currentPostQCol;
                thisResult="&nbsp;";
            } else if ( thisResult != null && thisResult.indexOf("@@")== 0) {
                classString="postheadercenter";
                thisResult ="query values"; //leave as follows for diagnostics: thisResult.substring(2);
                thisResult = thisResult.substring(2);
            } else {
                if ( postQHeaderCols[ thiscol ] == true )
                    classString = "postheaderright";
                else if ( thiscol == 1 && thisrow < 1 ) // jc55 was thisrow<2
                    classString = "rowbold";
                else
                    classString = "row";
                if ( thisResult == null ||  "".equals(thisResult) )
                    thisResult="&nbsp;";
            }
            if ( thiscol == 0 ) { // 1st column of new row
                if ( thisrow > 0 ) { // first must close prior row %>
                    </tr>
                        <%                  if ("XX".equals(thisResult) ) {
                        dropRow = true;
                    } %>
                    <tr valign="top" class="rowvert">  <!-- okay to start even a dropRow because it will get no <td> elements -->
<%              } else { %>
                    <tr valign="top" class="header">  <!-- okay to start even a dropRow because it will get no <td> elements -->
<%              }
                if ( !dropRow ) { %>
                    <td width="15%" class="verticalfieldlabel">
                    <%=thisResult%>
<%              }
            } else { // 2nd or higher column
                if ( !dropRow ) { %>
                    <td class="<%=classString%>" >
                        <%                  if ("XX".equals(thisResult) ) { %>
                        <%="&nbsp;"%>
<%                  } else { %>
                        <%=thisResult%>
<%                  }
                }
            }
            if ( !dropRow ) { %>
                </td>
<%          }
        }
    }  %>
    </tr>
    </table>
<%
} else {
    System.out.println("No results reported when " + rows + " rows and a result array size of " + results.size()); %>
    No results retrieved for query "<%=request.getAttribute("querystring")%>".
<%  Iterator errorIter = results.iterator();
    while ( errorIter.hasNext()) {
        String errorResult = (String)errorIter.next(); %>
        <p>Error returned: <%= errorResult%></p>
<%  }
} %>


