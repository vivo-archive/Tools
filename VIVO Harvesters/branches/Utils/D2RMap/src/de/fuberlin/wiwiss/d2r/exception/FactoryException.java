/*
 * The contents of this file are subject to the GNU Lesser General Public
 * License Version 2.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/lgpl-license.php
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is D2R Map.
 *
 * The Initial Developer of the Original Code is Plugged In Software Pty
 * Ltd (http://www.pisoftware.com, mailto:info@pisoftware.com). Portions
 * created by Plugged In Software Pty Ltd are Copyright (C) 2001,2002
 * Plugged In Software Pty Ltd. All Rights Reserved.
 *
 * Contributor(s): Robert Turner, Chris Bizer.
 *
 * [NOTE: The text of this Exhibit A may differ slightly from the text
 * of the notices in the Source Code files of the Original Code. You
 * should use the text of this Exhibit A rather than the text found in the
 * Original Code Source Code for Your Modifications.]
 *
 */
package de.fuberlin.wiwiss.d2r.exception;


/**
 * Exception used to indicate that there was an Exception raised in a Factory.
 *
 * @created 2004-05-26
 *
 * @author <a href="mailto:robert.turner@tucanatech.com">Robert Turner</a>
 *
 * @version $Revision: 1.7 $
 *
 * @modified $Date: 2004/05/21 04:51:54 $ by $Author: turnerrx $
 *
 * @company: <a href="http://www.tucanatech.com/">Tucana Technologies</a>
 *
 * @copyright &copy;2002-2003
 *   <a href="http://www.pisoftware.com/">Plugged In Software Pty Ltd</a>
 *
 */
public class FactoryException extends Exception {

  /**
   * Constructor, sets the message for the Exception
   */
  public FactoryException(String message) {

    super(message);
  }

  /**
   * Constructor, sets the message for the Exception and it's root cause.
   */
  public FactoryException(String message, Throwable cause) {

    super(message, cause);
  }

}
