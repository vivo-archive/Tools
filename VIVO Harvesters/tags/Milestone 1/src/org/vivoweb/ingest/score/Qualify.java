/*******************************************************************************
 * Copyright (c) 2010 Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams
 * 
 * This file is part of VIVO.
 * 
 * VIVO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VIVO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with VIVO.  If not, see <http://www.gnu.org/licenses/gpl-3.0.txt>.
 * 
 * Contributors:
 *     Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams - initial implementation
 ******************************************************************************/
package org.vivoweb.ingest.score;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

/***
 *  VIVO Qualify
 * 
 * @author Dale Scheppler - dscheppler@ichp.ufl.edu 
 ***/
public class Qualify {
	/**
	 * @param strWrite - String to write to logfile, automatically appends date and time.
	 */
	@SuppressWarnings("unused")
	private void writeToLog(String strWrite)
	{
		File fileLogFile = new File("logs/Log.txt");
		FileWriter fwOutFile;
		try
		{
			Calendar gcToday = Calendar.getInstance();
			int intYear = gcToday.get(Calendar.YEAR);
			int intMonth = gcToday.get(Calendar.MONTH) + 1;
			int intDay = gcToday.get(Calendar.DATE);
			int intHour = gcToday.get(Calendar.HOUR_OF_DAY);
			int intMinute = gcToday.get(Calendar.MINUTE);
			int intSecond = gcToday.get(Calendar.SECOND);
			int intMillisecond = gcToday.get(Calendar.MILLISECOND);
			fwOutFile = new FileWriter(fileLogFile, true);
			PrintWriter out = new PrintWriter(fwOutFile);
			out.println("Qualify:" + intYear + "/" + intMonth + "/" + intDay + "/" + intHour + ":" + intMinute + ":" + intSecond + "." + intMillisecond + "-" + strWrite);
			out.close();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
}

