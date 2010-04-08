//
// Jar2Lib.java
//

/*
LOCI autogen package for programmatically generating source code.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.util.Set;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.HashSet;

import java.lang.String;

import jace.autoproxy.AutoProxy;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
* Automatically generates a C++ header file for use with Jace, listing all
* classes within the given component. As of this writing, this functionality
* is mainly used to generate bio-formats.h for the Bio-Formats C++ bindings,
* but could in principle be used to generate a Jace-friendly list of classes
* for any of LOCI's Java components.
*
* <dl><dt><b>Source code:</b></dt>
* <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/auto/Jar2Lib.java">Trac</a>,
* <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/auto/Jar2Lib.java">SVN</a></dd></dl>
*
* @author Brian Selinsky bselinsky at wisc.edu
*/
public class Jar2Lib {

	private final String headerFileName;
	private final String headerInputPath;
	private final String sourceInputPath;
	private final String headerOutputPath;
	private final String sourceOutputPath;
	private final String classPath;
	private final boolean mindep;
	private final boolean exportSymbols;
	private final Set<String> dependencyList;

	
	public Jar2Lib(
		String headerFileName,
		String headerInputPath,
		String sourceInputPath,
		String headerOutputPath,
		String sourceOutputPath,
		String classPath,
		boolean mindep,
		boolean exportSymbols,
		String dependencies,
		String dependencyFile) {

		this.headerFileName = headerFileName;
		this.headerInputPath = headerInputPath;
		this.sourceInputPath = sourceInputPath;
		this.headerOutputPath = headerOutputPath;
		this.sourceOutputPath = sourceOutputPath;
		this.classPath = classPath;
		this.mindep = mindep;
		this.exportSymbols = exportSymbols;
		this.dependencyList = new HashSet<String>();

		if ( (dependencies != null) && (dependencies.length() != 0) ) {
			StringTokenizer st = new StringTokenizer(dependencies, "=");
			st.nextToken();
			String depList = st.nextToken();
			st = new StringTokenizer(depList, ",");
		
			while (st.hasMoreTokens()) {
				dependencyList.add(st.nextToken());
			}
		}

		if (dependencyFile != null) {
			File depFile = new File(dependencyFile);
			if ( (depFile.exists()) && (depFile.exists()) ) {
				try {
					BufferedReader reader = new BufferedReader(new FileReader(depFile));
					String input;
					while ( (input = reader.readLine()) != null) {
						dependencyList.add(input);
					}
				} catch (java.io.FileNotFoundException fnfe) {
					// Shouldn't get here
				} catch (java.io.IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}

	public void createJaceHeader() {
		String headerFile = headerFileName + ".h";
		String headerLabel = headerFile.toUpperCase().replaceAll("\\W", "_");
	
		// initialize Velocity
		VelocityEngine ve = null;
		VelocityContext context = null;
		try {
			ve = VelocityTools.createEngine();
			context = VelocityTools.createContext();
		} catch (Exception e) {
			System.err.println("Exception caught from Velocity");
			e.printStackTrace();
		}
	
		// parse header file template
		SourceList javaList = null;
		try {
			javaList = new SourceList(sourceInputPath);
		} catch (IOException ioe) {
			System.err.println("IO Exception caught from SourceList");
			ioe.printStackTrace();
		}
	
		context.put("headerFile", headerFile);
		context.put("headerLabel", headerLabel);
		context.put("q", javaList);
	
		// generate C++ header file
		try {
			VelocityTools.processTemplate(ve, context, "jace/header.vm", headerFile);
		} catch (Exception e) {
				System.err.println("Exception caught from VelocityTools.processTemplate");
				e.printStackTrace();
		}
	}

	public void runAutoProxy() {

		AutoProxy ap =
			new AutoProxy(
				headerInputPath,
				sourceInputPath,
				headerOutputPath,
				sourceOutputPath,
				classPath,
				mindep,
				dependencyList,
				exportSymbols);
		System.out.println("Beginning Proxy generation.");
		try {
			ap.generateProxies();
		} catch (Exception e) {
				System.err.println("Exception caught from generateProxies");
				e.printStackTrace();
		}
		System.out.println("Finished Proxy generation.");
	}

	// -- Main method --
	public static void main(String[] args) throws Exception {
		String headerFileName = "";
		String sourceInputPath = "";
		String headerInputPath = "";
		String headerOutputPath = "";
		String sourceOutputPath = "";
		String classPath = "";
		boolean mindep = false;
		boolean exportSymbols = false;
		String dependencies = "";
		String dependencyFile = "";

		boolean headerInputPathDefined = false;
		boolean headerOutputPathDefined = false;
		boolean sourceInputPathDefined = false;
		boolean sourceOutputPathDefined = false;
		boolean headerFileNameDefined = false;
		boolean classPathDefined = false;

		GetOpt go = new GetOpt(args, "h:s:H:S:C:f:D:em");
		go.optErr = true;
		int ch = -1;
		// process options in command line arguments
		while ((ch = go.getopt()) != go.optEOF) {
			// System.out.println("Processing " + (char)ch);
			if (ch == 'h') {
				headerInputPath = go.optArgGet();
				headerInputPathDefined = true;
			} else if (ch == 's') {
				sourceInputPath = go.optArgGet();
				sourceInputPathDefined = true;
			} else if (ch == 'H') {
				headerOutputPath = go.optArgGet();
				headerOutputPathDefined = true;
			} else if (ch == 'S') {
				sourceOutputPath = go.optArgGet();
				sourceOutputPathDefined = true;
			} else if (ch == 'C') {
				classPath = go.optArgGet();
				classPathDefined = true;
			} else if (ch == 'D') {
				dependencyFile = go.optArgGet();
			} else if (ch == 'd') {
				dependencies = go.optArgGet();
			} else if (ch == 'f') {
				headerFileName = go.optArgGet();
				headerFileNameDefined = true;
			} else if (ch == 'm') {
				mindep = true;
			} else if (ch == 'e') {
				exportSymbols = true;
			} else {
				System.err.println("Illegal option " + ch);
				System.exit(1);
			}
		}

		if (!(headerInputPathDefined && headerInputPathDefined &&
			sourceOutputPathDefined && sourceOutputPathDefined &&
			headerFileNameDefined && classPathDefined))
		{
			System.out.println("Add a usage statement");
			System.exit(1);
		}

		Jar2Lib jar2Lib =
			new Jar2Lib(
				headerFileName,
				headerInputPath,
				sourceInputPath,
				headerOutputPath,
				sourceOutputPath,
				classPath,
				mindep,
				exportSymbols,
				dependencies,
				dependencyFile);

		jar2Lib.createJaceHeader();

		jar2Lib.runAutoProxy();
	}
}
