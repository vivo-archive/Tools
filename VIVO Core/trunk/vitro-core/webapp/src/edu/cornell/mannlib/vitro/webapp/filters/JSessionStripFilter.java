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

package edu.cornell.mannlib.vitro.webapp.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Replaces the Response with one that will never put in a jsession.

 * Here is what needs to go into the web.xml:
 *
    <filter>
        <filter-name>JSession Strip Filter</filter-name>
        <filter-class>edu.cornell.mannlib.vitro.filters.JSessionStripFilter</filter-class>
    </filter>

    <filter-mapping>
        <filter-name>JSession Strip Filter</filter-name>
        <url-pattern>/*</url-pattern>
        <dispatcher>REQUEST</dispatcher>
    </filter-mapping>

 * some of this code is from URLRewriteFilter
 */
public class JSessionStripFilter implements Filter {
    private FilterConfig filterConfig = null;

    private static final Log log = LogFactory.getLog(JSessionStripFilter.class.getName());
    public static String USING_JSESSION_STRIP = "usingJsessionStrip";

    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Filtering: no jsessionids will be generated.");
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse hResponse = (HttpServletResponse)response;
        HttpServletRequest hRequest = (HttpServletRequest) request;
        hRequest.setAttribute(USING_JSESSION_STRIP, "true");

        if (hResponse.isCommitted()) {
            log.error("response is comitted cannot forward " +
                      " (check you haven't done anything to the response (ie, written to it) before here)");
            return;
        }
        chain.doFilter(hRequest, new StripSessionIdWrapper( hResponse) );

    }

    public void destroy() {
        filterConfig = null;
    }

    /**
     * This is a wrapper that does not encode urls with jsessions
     */
    public class StripSessionIdWrapper extends HttpServletResponseWrapper {
        public StripSessionIdWrapper(HttpServletResponse response)
        {
            super(response);
        }

        /**
         * @deprecated
         */
        public String encodeRedirectUrl(String url) { return (url); }
        public String encodeRedirectURL(String url) { return (url); }

        /**
         * @deprecated
         */
        public String encodeUrl(String url) { return (url); }

        public String encodeURL(String url) { return (url); }

        /**
         * @deprecated
         */
        public void setStatus(int sc, String sm) {super.setStatus(sc, sm); }

    }
}
