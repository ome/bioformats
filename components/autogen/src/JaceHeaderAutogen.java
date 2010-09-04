//
// JaceHeaderAutogen.java
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

/**
 * Automatically generates a C++ header file for use with Jace, listing all
 * classes within the given component. As of this writing, this functionality
 * is mainly used to generate bio-formats.h for the Bio-Formats C++ bindings,
 * but could in principle be used to generate a Jace-friendly list of classes
 * for any of LOCI's Java components.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/bio-formats/auto/JaceHeaderAutogen.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/bio-formats/auto/JaceHeaderAutogen.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class JaceHeaderAutogen {

  // -- Main method --

  public static void main(String[] args) throws Exception {
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
    SourceList javaList = new SourceList(sourceDir);

    context.put("headerFile", headerFile);
    context.put("headerLabel", headerLabel);
    context.put("q", javaList);

    // generate C++ header file
    VelocityTools.processTemplate(ve, context, "jace/header.vm", headerFile);
  }

}
