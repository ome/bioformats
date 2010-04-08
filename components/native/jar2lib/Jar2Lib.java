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
* @author Curtis Rueden ctrueden at wisc.edu
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
		String dependencies) {

		this.headerFileName = headerFileName;
		this.headerInputPath = headerInputPath;
		this.sourceInputPath = sourceInputPath;
		this.headerOutputPath = headerOutputPath;
		this.sourceOutputPath = sourceOutputPath;
		this.classPath = classPath;
		this.mindep = mindep;
		this.exportSymbols = exportSymbols;

		StringTokenizer st = new StringTokenizer(dependencies, "=");
		// st.nextToken();
		// String depList = st.nextToken();
		// st = new StringTokenizer(depList, ",");
	
		dependencyList = new HashSet<String>();
		// while (st.hasMoreTokens()) {
			// dependencyList.add(st.nextToken());
		// }
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
		if (args.length < 2) {
			System.out.println(
				"Usage: java JaceHeaderAutogen component-name source-dir");
			System.out.println("    E.g.: java JaceHeaderAutogen " +
				"bio-formats ~/svn/java/components/bio-formats/src");
			System.exit(1);
		}

		String headerFileName = args[0];
		String sourceInputPath = args[1];
		String headerInputPath = args[2];
		String headerOutputPath = args[3];
		String sourceOutputPath = args[4];
		String classPath = args[5];
		boolean mindep = false;
		boolean exportSymbols = false;
		String dependencies = "";

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
				dependencies);

		jar2Lib.createJaceHeader();

		jar2Lib.runAutoProxy();
	}
}
