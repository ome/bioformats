/*
 * #%L
 * Bio-Formats autogen package for programmatically generating source code.
 * %%
 * Copyright (C) 2007 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import java.io.File;
import java.io.IOException;

import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * Automatically generates a report on supported metadata fields
 * for each file format reader in Bio-Formats.
 *
 * Uses data from the meta-support.txt and format-pages.txt files.
 *
 * TODO - Add support for group reporting using meta-groups.txt.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class MetaSupportAutogen {

  private static final String FORMAT_PAGES = "format-pages.txt";

  // -- Main method --

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("Usage: java MetaSupportAutogen ome-xml-version");
      System.out.println("    E.g.: java MetaSupportAutogen 2012-06");
      System.exit(1);
    }
    String version = args[0];

    // create needed directories
    File doc = new File("doc");
    if (!doc.exists()) {
      boolean success = doc.mkdir();
      if (!success) {
        throw new IOException("Could not create " + doc.getAbsolutePath());
      }
    }
    File docMeta = new File(doc, "meta");
    if (!docMeta.exists()) {
      boolean success = docMeta.mkdir();
      if (!success) {
        throw new IOException("Could not create " + docMeta.getAbsolutePath());
      }
    }

    // initialize Velocity
    VelocityEngine ve = VelocityTools.createEngine();
    VelocityContext context = VelocityTools.createContext();

    // parse supported properties list
    MetaSupportList supportList = new MetaSupportList(version);
    context.put("q", supportList);

    // retrieve the table of format page names
    IniParser parser = new IniParser();
    parser.setCommentDelimiter(null);
    IniList data = parser.parseINI(FORMAT_PAGES, MetaSupportAutogen.class);

    for (String handler : supportList.handlers()) {
      supportList.setHandler(handler);
      supportList.setPageName("metadata/" + handler);
    }

    // generate master table of metadata properties
    VelocityTools.processTemplate(ve, context, "doc/meta-summary.vm",
      "../../docs/sphinx/metadata-summary.rst");

    // generate metadata property support documentation for each handler
    for (String handler : supportList.handlers()) {
      supportList.setHandler(handler);

      String pagename = supportList.getPageName();
      if (pagename != null) {
        VelocityTools.processTemplate(ve, context, "doc/MetadataSupport.vm",
          "../../docs/sphinx/" + pagename + ".rst");
      }
    }
  }

}
