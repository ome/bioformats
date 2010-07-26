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
import java.util.Iterator;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.HashSet;
import java.util.jar.JarFile;

import java.lang.String;

import jace.autoproxy.AutoProxy;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
* <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/native/jar2lib/Jar2Lib.java">Trac</a>,
* <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/native/jar2lib/Jar2Lib.java">SVN</a></dd></dl>
*
* @author Brian Selinsky bselinsky at wisc.edu
*/
public class Jar2Lib {

	private final String headerFileName;
	private final String headerInputPath;
	private final String sourceInputPath;
	private final String headerOutputPath;
	private final String sourceOutputPath;
	private final String jarFileName;
	private final String classPath;
	private final String extraCppFiles;
	private final String includePaths;
	private final String conflictsFile;
	private final String jniLibPath;
	private final boolean mindep;
	private final boolean exportSymbols;
	private final Set<String> dependencyList;
	static private String REPLACE = "# Automatically replaced";
	static private String REPLACE2 = "JNILIB_PATH_GOES_HERE";

	
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
		String dependencyFile,
		String conflictsFile,
		String jarFileName,
		String extraCppFiles,
		String includePaths,
		String jniLibPath) {

		this.headerFileName = "out/src/" + headerFileName;
		this.headerInputPath = headerInputPath;
		this.sourceInputPath = sourceInputPath;
		this.headerOutputPath = headerOutputPath;
		this.sourceOutputPath = sourceOutputPath;
		this.classPath = classPath;
		this.mindep = mindep;
		this.exportSymbols = exportSymbols;
		this.dependencyList = new HashSet<String>();
		this.conflictsFile = conflictsFile;
		this.jarFileName = jarFileName;
		this.extraCppFiles = extraCppFiles;
		this.includePaths = includePaths;
		this.jniLibPath = jniLibPath;

System.out.println("HeaderInputPath is " + headerOutputPath);
System.out.println("sourceInputPath is " + sourceOutputPath);

		if ( (dependencies != null) && (dependencies.length() != 0) ) {
			StringTokenizer st = new StringTokenizer(dependencies, "=");
			if (st.hasMoreElements()) {
				st.nextToken();
				String depList = st.nextToken();
				st = new StringTokenizer(depList, ",");
			
				while (st.hasMoreTokens()) {
					String token = (String)st.nextToken();
					try {
						Class.forName(token);
						dependencyList.add(token);
					} catch (ClassNotFoundException cnfe) {
						System.out.println("Ignoring exception: " +
							cnfe.toString());
					}
				}
			}
		}

		if (dependencyFile != null) {
			File depFile = new File(dependencyFile);
			if ( (depFile.exists()) && (depFile.exists()) ) {
				try {
					BufferedReader reader = new BufferedReader(new FileReader(depFile));
					String input;
					while ( (input = reader.readLine()) != null) {
						if (!input.startsWith("#")) {
							if (input.trim().length() > 0) {
								System.out.println("### " + input);
								dependencyList.add(input);
							}
						}
					}
				} catch (java.io.FileNotFoundException fnfe) {
					// Shouldn't get here
				} catch (java.io.IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}

	public void createCMakeList() {
		String headerFile = "CMakeLists.txt";
		String headerLabel = headerFileName;

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
		SourceList cList = null;
		try {
			cList = new SourceList();
			cList.add(sourceOutputPath, ".c:.cxx:.cpp");
			if (extraCppFiles != null) {
				String[] cppFiles = extraCppFiles.split(":");
				File f;
				for (int i = 0; i < cppFiles.length; i++) {
					f = new File(cppFiles[i]);
					if (f.isDirectory()) {
						cList.add(cppFiles[i], ".c:.cxx:.cpp");
					} else if (cppFiles[i].endsWith(".jar")) {
						cList.add(new JarFile(cppFiles[i]));
					} else { // Plain file
						cList.add(cppFiles[i]);
					}
				}
			}
		} catch (IOException ioe) {
			System.err.println("IO Exception caught from SourceList");
			ioe.printStackTrace();
		}

		Iterator i = cList.files().iterator();

		context.put("headerFile", headerFile);
		context.put("headerLabel", headerLabel);
		context.put("q", cList);
	
		// generate CMakeLists.txt file
		try {
			VelocityTools.processTemplate(ve, context, "jace/cmake.vm", headerFile);
		} catch (Exception e) {
				System.err.println("Exception caught from VelocityTools.processTemplate");
				e.printStackTrace();
		}
	}

	public void createJaceHeader() {
		String headerFile = headerFileName + ".h";
		String headerLabel = headerFile.toUpperCase().replaceAll("\\W", "_");

		//
		// Add include paths to the cmake.vm file
		//
		File orig = new File("jace/cmake.vm.orig");

		try {
			File after = new File("jace/cmake.vm");
			FileWriter writer = new FileWriter(after);
	
			BufferedReader reader = new BufferedReader(new FileReader(orig));
			String s = "";
			String input;
			while ( (input = reader.readLine()) != null) {
				s += input + "\n";
			}

			if (includePaths != null) {
				int loc = s.indexOf(REPLACE);
		
				String[] includes = includePaths.split(":");
				String changed = s.substring(0, loc - 1);
				for (int i = 0; i < includes.length; i++) {
    				changed += "\n	include_directories(\"" + includes[i] + "\")";
				}
				changed += s.substring(loc + REPLACE.length());

				int loc2 = changed.indexOf(REPLACE2);
				String changed2 = changed.substring(0, loc2);
				changed2 += jniLibPath + "/";
                changed2 += changed.substring(loc2 + REPLACE2.length() + 1);

				writer.write(changed2);
			} else {
				int loc2 = s.indexOf(REPLACE2);
				String changed2 = s.substring(0, loc2);
				changed2 += jniLibPath + "/";
                changed2 += s.substring(loc2 + REPLACE.length() + 1);

				writer.write(s);
			}
			writer.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
	
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
			if (sourceInputPath.length() > 0) {
				String[] paths = sourceInputPath.split(":");
				javaList = new SourceList();
				File f;
				for (int i = 0; i < paths.length; i++) {
					f = new File(paths[i]);
					if (f.isDirectory()) {
						javaList.add(paths[i], ".java");
					} else if (paths[i].endsWith(".jar")) {
						javaList.add(new JarFile(paths[i]));
					} else { // Plain file
						javaList.add(paths[i]);
					}
				}
			} else if (jarFileName != null) {
				javaList = new SourceList(new JarFile(jarFileName));
			} else {
				// Shouldn't get here
			}
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

	private void runFixProxies() {
		try {
			new FixProxies(headerOutputPath, conflictsFile).fixProxies();
			new FixProxies(sourceOutputPath, conflictsFile).fixProxies();
		} catch (IOException ioe) {
			System.out.println("Got an IO Exception");
			System.out.println(ioe.toString());
			ioe.printStackTrace();
		}
	}

	public void runAutoProxy() {

		AutoProxy ap =
			new AutoProxy(
				headerOutputPath + "/include",
				sourceOutputPath + "/source",
				headerOutputPath + "/include",
				sourceOutputPath + "/source",
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

	private static void printUsageStatement() {
		System.out.println("Usage: java JarFile");
		System.out.println("	[ -C classpath ]");
		System.out.println("	[ -d dependencies comma seperated list ]");
		System.out.println("	[ -D dependencies file ]");
		System.out.println("	[ -e /* export symbols */ ]");
		System.out.println("	-f File name for header file");
		System.out.println("	-h Header file input path");
		System.out.println("	-H Header file output path");
		System.out.println("	[ -j Jar file ]");
		System.out.println("	[ -m /* mindep  */ ]");
		System.out.println("	-s Source file input path");
		System.out.println("	-S Source file output path");
		System.out.println("	[ -? Print this statement ]");
	}

	// -- Main method --
	public static void main(String[] args) throws Exception {
		String headerFileName = "";
		String sourceInputPath = "";
		String headerInputPath = "";
		String headerOutputPath = "";
		String sourceOutputPath = "";
		String classPath = "";
		boolean mindep = true;
		boolean exportSymbols = false;
		String dependencies = "";
		String dependencyFile = "";
		String conflictsFile = "";
		String jarFileName = null;
		String jniLibPath = null;
		String extraCppFiles = null;
		String includePaths = null;

		boolean headerInputPathDefined = false;
		boolean headerOutputPathDefined = false;
		boolean sourceInputPathDefined = false;
		boolean sourceOutputPathDefined = false;
		boolean headerFileNameDefined = false;
		boolean classPathDefined = false;

for (int i = 0; i < args.length; i++) {
System.out.println("WWWW " + args[i]);
}
		GetOpt go = new GetOpt(args, "L:I:x:h:s:H:S:c:C:f:d:D:j:em?");
		go.optErr = true;
		int ch;
		// process options in command line arguments
		while ((ch = go.getopt()) != go.optEOF) {
System.out.println("XXXX " + ((char)ch));
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
			} else if (ch == 'c') {
				conflictsFile = go.optArgGet();
			} else if (ch == 'D') {
				dependencyFile = go.optArgGet();
			} else if (ch == 'd') {
				dependencies = go.optArgGet();
			} else if (ch == 'j') {
				jarFileName = go.optArgGet();
			} else if (ch == 'L') {
				jniLibPath = go.optArgGet();
			} else if (ch == 'f') {
				headerFileName = go.optArgGet();
				headerFileNameDefined = true;
			} else if (ch == 'm') {
				mindep = false;
			} else if (ch == 'x') {
				extraCppFiles = go.optArgGet();
			} else if (ch == 'I') {
				includePaths = go.optArgGet();
			} else if (ch == 'e') {
				exportSymbols = true;
			} else if (ch == '?') {
				Jar2Lib.printUsageStatement();
				System.exit(1);
			} else {
				System.err.println("Illegal option " + ch);
				Jar2Lib.printUsageStatement();
				System.exit(1);
			}
		}

		if (!(headerInputPathDefined && headerOutputPathDefined &&
			sourceInputPathDefined && sourceOutputPathDefined &&
			headerFileNameDefined && classPathDefined))
		{
			System.out.println("The following options are required ");
			System.out.println("	-f Header File Name");
			System.out.println("	-h Header Input Path");
			System.out.println("	-H Header Output Path");
			System.out.println("	-s Source Input Path");
			System.out.println("	-S Source Output Path");
			Jar2Lib.printUsageStatement();
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
				dependencyFile,
				conflictsFile,
				jarFileName,
				extraCppFiles,
				includePaths,
				jniLibPath);

		jar2Lib.createJaceHeader();

		jar2Lib.runAutoProxy();

		jar2Lib.runFixProxies();

		jar2Lib.createCMakeList();
	}
}
