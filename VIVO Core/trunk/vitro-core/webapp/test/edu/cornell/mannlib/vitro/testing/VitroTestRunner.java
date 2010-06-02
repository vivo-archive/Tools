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

package edu.cornell.mannlib.vitro.testing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.runner.JUnitCore;

/**
 * A Java application that will run the Vitro unit tests. It searches for unit
 * tests in the supplied source directory (any file whose name is *Test.Java).
 * It runs the tests with a variety of reporting detail, depending on the level
 * selected. If the level selector is absent or unrecognized, the medium level
 * is used.
 * 
 * @author jeb228
 */
public class VitroTestRunner {
	public enum ReportLevel {
		/** Report only the one-line summary. */
		BRIEF,
		/** Report times and statistics for each test class. */
		MORE,
		/** Report times and statistics for each test method. */
		FULL
	}

	private final List<Class<?>> classes;
	private final VitroTestRunListener listener;

	/**
	 * Locate the test classes. Initialize the test listener.
	 */
	public VitroTestRunner(File sourceRootDir, ReportLevel reportLevel) {
		List<String> classNames = getListOfTestClassNames(sourceRootDir);
		this.classes = getTestClasses(classNames);
		this.listener = new VitroTestRunListener(reportLevel);
	}

	/**
	 * Start a recursive search through the source directory.
	 */
	private List<String> getListOfTestClassNames(File sourceRootDir) {
		SortedSet<String> names = new TreeSet<String>();
		searchForTestClasses(names, "", sourceRootDir);
		return new ArrayList<String>(names);
	}

	/**
	 * Recursively search the directory for files in the form "*Test.java".
	 * Ignore any files or directories whose names start with a "."
	 */
	private void searchForTestClasses(SortedSet<String> names, String prefix,
			File directory) {
		for (File file : directory.listFiles()) {
			String filename = file.getName();
			if (filename.startsWith(".")) {
				// ignore .svn, etc.
			} else if (file.isDirectory()) {
				searchForTestClasses(names, prefix + filename + ".", file);
			} else if (filename.endsWith("Test.java")) {
				String classname = filename.substring(0, filename.length() - 5);
				names.add(prefix + classname);
			}
		}
	}

	/**
	 * Instantiate a class for each test class name.
	 */
	private List<Class<?>> getTestClasses(List<String> classNames) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (String classname : classNames) {
			try {
				classes.add(Class.forName(classname));
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("Can't load test class: "
						+ classname, e);
			}
		}
		return classes;
	}

	/**
	 * We've located all of the test clases. Now run them.
	 */
	private void run() {
		JUnitCore junitCore = new JUnitCore();
		junitCore.addListener(this.listener);
		junitCore.run(this.classes.toArray(new Class<?>[0]));
	}

	/**
	 * Did any of the tests fail?
	 */
	private boolean didEverythingPass() {
		return this.listener.didEverythingPass();
	}

	/**
	 * <p>
	 * You must provide a path to the source directory of the test classes.
	 * </p>
	 * <p>
	 * You may also provide a reporting level of "BRIEF", "MORE", or "FULL". If
	 * no level is provided, or if it is not recognized, "BRIEF" is used.
	 * </p>
	 */
	public static void main(String[] args) {
		if ((args.length < 1) || (args.length > 2)) {
			usage("Wrong number of arguments: expecting 1 or 2, but found "
					+ args.length + ".");
		}
		File sourceRootDir = new File(args[0]);

		if (!sourceRootDir.exists()) {
			usage(sourceRootDir + " does not exist.");
		}
		if (!sourceRootDir.isDirectory()) {
			usage(sourceRootDir + " is not a directory.");
		}

		ReportLevel reportLevel = ReportLevel.MORE;
		if (args.length == 2) {
			for (ReportLevel level : ReportLevel.values()) {
				if (level.toString().equalsIgnoreCase(args[1])) {
					reportLevel = level;
				}
			}
		}

		VitroTestRunner runner = new VitroTestRunner(sourceRootDir, reportLevel);
		runner.run();

		if (!runner.didEverythingPass()) {
			System.exit(1);
		}
	}

	/**
	 * Tell them how it should have been done.
	 */
	private static void usage(String message) {
		System.out.println(message);
		System.out.println("usage: " + VitroTestRunner.class.getSimpleName()
				+ " sourceRootDirectory  [ BRIEF | MORE | FULL ]");
		System.exit(1);
	}
}
