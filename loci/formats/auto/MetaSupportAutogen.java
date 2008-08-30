//
// MetaSupportAutogen.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.auto;

import java.io.*;
import java.text.DateFormat;
import java.util.*;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

/**
 * Automatically generates a report on 
 * Automatically generates code for the MetadataStore and MetadataRetrieve
 * interfaces, as well as the implementations for various flavors of OME-XML.
 *
 * Uses data from the meta-groups.txt and meta-support.txt files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/auto/MetaSupportAutogen.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/auto/MetaSupportAutogen.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class MetaSupportAutogen {

  // -- Main method --

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println(
        "Usage: java loci.formats.auto.MetaSupportAutogen ome-xml-version");
      System.out.println(
        "    E.g.: java loci.formats.auto.MetaSupportAutogen 2008-02");
      System.exit(1);
    }
    String version = args[0];

    // create needed directories
    File doc = new File("doc");
    if (!doc.exists()) doc.mkdir();
    File docMeta = new File(doc, "meta");
    if (!docMeta.exists()) docMeta.mkdir();

    // initialize Velocity
    VelocityEngine ve = VelocityTools.createEngine();
    VelocityContext context = VelocityTools.createContext();

    // parse supported properties list
    MetaSupportList supportList = new MetaSupportList(version);
    context.put("q", supportList);

    // generate master table of metadata properties
    VelocityTools.processTemplate(ve, context, "meta-summary.vm",
      "doc/meta-summary.html");

    // generate metadata property support documentation for each handler
    for (String handler : supportList.handlers()) {
      supportList.setHandler(handler);
      VelocityTools.processTemplate(ve, context, "MetaSupportWikiPage.vm",
        "doc/meta/" + handler + ".txt");
    }
  }

}
