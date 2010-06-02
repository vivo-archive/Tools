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

/*
 ******************************************************************************
 *
 * user-extensions.js
 *
 * Vivo-specific modifications to Selenium IDE (1.0.4) and Selenium RC (1.0.2)
 *
 * The file "ide-extensions.js" will modify Selenium IDE so comments are 
 * written as "comment" commands. This makes them visible when the HTML test
 * file is viewed in a browser.
 *
 * This file creates a "comment" command (that does nothing) so the Selenium 
 * engine will not throw an error when running those HTML test files.
 *
 * To install these mods in Selenium IDE:
 * 1) Start the IDE.
 * 2) Go the "Options" menu and select "Options..."
 * 3) Put the path to "user-extensions.js into the 
 *    "Selenium core extensions" field.
 * 4) Put the path to this file "ide-extensions.js" into the 
 *    "Selenium IDE extensions" field.
 * 5) Close the IDE and re-start it.
 *
 * To run Selenium RC with these mods:
 * 1) Add the "-userExtensions [file]" option to the command line, with [file]
 *    containing the path to "user-extension.js".
 *
 * For example: (this should be a single line)
 *    java -jar "C:\Vitro_stuff\Selenium\sauceRC-selenium-server.jar" 
 *         -singleWindow 
 *         -htmlSuite 
 *              "*firefox" 
 *              [URL of Vivo site] 
 *              [path to test suite] 
 *              [path to output file]
 *         -firefoxProfileTemplate [path to filefox profile]
 *         -timeout 600 
 *         -userExtensions [path to user-extensions.js]
 *
 ******************************************************************************
 */

/*
 * Create "comment" as a command that does nothing.
 */
Selenium.prototype.doComment = function(){};
