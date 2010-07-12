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

package edu.cornell.mannlib.vitro.webapp.servlet.setup;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.utils.ThemeUtils;

public class DefaultThemeSetup implements ServletContextListener {
	
	// Set default theme based on themes present on the file system
	public void contextInitialized(ServletContextEvent event) {

    	// Find the themes directory in the file system
		ServletContext sc = event.getServletContext();		
    	boolean doSort = true;
    	ArrayList<String> themeNames = ThemeUtils.getThemes(sc, doSort);
        
        String defaultTheme;
        if (themeNames.contains("enhanced")) {
        	defaultTheme = "enhanced";
        }
        else if (themeNames.contains("default")) {
        	defaultTheme = "default";
        }
        else {
        	defaultTheme = themeNames.get(0);
        }
        
        String defaultThemeDir = "themes/" + defaultTheme + "/";
        // Define as a static variable of Portal so getThemeDir() method of portal can access it.
        Portal.DEFAULT_THEME_DIR_FROM_CONTEXT = defaultThemeDir;
        
	}

	public void contextDestroyed(ServletContextEvent event) {
		// nothing to do here
	}
}
