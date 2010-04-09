//
// SourceList.java
//

/*
	LOCI autogen package for programmatically generating source code.
	Copyright (C) 2005-2010 UW-Madison LOCI and Glencoe Software, Inc.

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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.Enumeration;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
* A SourceList is a list of source files rooted at a particular directory.
*
* <dl><dt><b>Source code:</b></dt>
* <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/auto/SourceList.java">Trac</a>,
* <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/auto/SourceList.java">SVN</a></dd></dl>
*
* @author Curtis Rueden ctrueden at wisc.edu
*/
public class SourceList {

	// -- Fields --

	/** Root directory prefix. */
	private String rootDir;

	/** The list of source files, relative to the root directory. */
	private Vector<String> files;

	// -- Constructor --

	/** Constructs list of source files rooted at the given directory. */
	public SourceList(JarFile jarFile) throws IOException {
		// list Java source files
		files = new Vector<String>();
		buildList(jarFile);
	}

	/** Constructs list of source files rooted at the given directory. */
	public SourceList(String rootDir) throws IOException {
		// list Java source files
		files = new Vector<String>();
		setRoot(rootDir);
	}

	// -- SourceList API methods --

	/** Changes the list to correspond to the given root directory. */
	public void setRoot(String rootDir) {
		rootDir = rootDir.replace(File.separatorChar, '/'); // Windows stupidity
		this.rootDir = rootDir;
		files.removeAllElements();
		listFiles(new File(rootDir));
		Collections.sort(files);
	}

	/** Changes the list to correspond to the given root directory. */
	public void buildList(JarFile jarFile) {
		this.rootDir = rootDir;
		files.removeAllElements();
		listFiles(jarFile);
		Collections.sort(files);
	}

	/** Gets the list of source files, relative to the root directory. */
		public Vector<String> files() {
		return files;
	}

	// -- Helper methods --

	/** Recursively finds all Java source files. */
	private void listFiles(JarFile jarFile) {
		Enumeration<JarEntry> jarEntries = jarFile.entries();
		if (jarEntries == null)
			return;

		while (jarEntries.hasMoreElements()) {
			JarEntry entry = jarEntries.nextElement();
			String name = entry.getName();
			if (name.toLowerCase().endsWith(".class")) {
				int index = name.lastIndexOf(".class");
				String javaName = name.substring(0, index) + ".java";
				files.add(javaName);
			}
		}
	}

	/** Recursively finds all Java source files. */
	private void listFiles(File dir) {
		File[] list = dir.listFiles();
		if (list == null) {
			return;
		}
		for (File f : list) {
			if (f.isDirectory()) {
				listFiles(f);
			}
			String path = f.getPath();
			path = path.replace(File.separatorChar, '/'); // Windows stupidity
			if (!path.startsWith(rootDir)) {
				continue;
			}
			if (!path.toLowerCase().endsWith(".java")) {
				continue;
			}
			path = path.substring(rootDir.length() + 1); // relative to root path
			files.add(path);
		}
	}

	// -- Utility methods --

	public static String packageName(String path) {
	int slash = path.lastIndexOf("/");
		return slash < 0 ? "" : path.substring(0, slash);
	}

	/** Returns true iff the two files are from different directories. */
	public static boolean isNewPackage(String path1, String path2) {
		return !packageName(path1).equals(packageName(path2));
	}

	public static String header(String path) {
		return path.replace(".java", ".h");
	}

	public static String namespace(String path) {
		return packageName(path).replaceAll("/", "::");
	}

	public static void main(String[] args) throws Exception {
		try {
			if (args.length < 2) {
				System.out.println(
					"Usage: java JaceHeaderAutogen component-name source-dir");
				System.out.println("    E.g.: java JaceHeaderAutogen " +
					"bio-formats ~/svn/java/components/bio-formats/src");
				System.exit(1);
			}
			String component = args[0];
			String sourceDir = args[1];
		
			String headerFile = component + ".h";
			String headerLabel = headerFile.toUpperCase().replaceAll("\\W", "_");
		
			// initialize Velocity
			VelocityEngine ve = VelocityTools.createEngine();
			VelocityContext context = VelocityTools.createContext();
		
			// parse header file template
			JarFile jf = new JarFile(sourceDir);
			SourceList javaList = new SourceList(jf);
		
			context.put("headerFile", headerFile);
			context.put("headerLabel", headerLabel);
			context.put("q", javaList);
		
			// generate C++ header file
			VelocityTools.processTemplate(ve, context, "jace/header.vm", headerFile);
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}
}
