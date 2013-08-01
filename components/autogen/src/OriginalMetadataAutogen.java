/*
 * #%L
 * Bio-Formats autogen package for programmatically generating source code.
 * %%
 * Copyright (C) 2007 - 2013 Open Microscopy Environment:
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import loci.common.DataTools;
import loci.formats.FormatException;
import loci.formats.ImageReader;
import loci.formats.UnknownFormatException;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/autogen/src/OriginalMetadataAutogen.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/autogen/src/OriginalMetadataAutogen.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class OriginalMetadataAutogen {

  // -- Constants --

  private static final String TEMPLATE = "doc/OriginalMetadataSupport.vm";

  // -- Fields --

  private HashMap<String, HashMap<String, ArrayList>> metadata =
    new HashMap<String, HashMap<String, ArrayList>>();
  private ImageReader reader = new ImageReader();
  private String version;

  // -- Constructor --

  public OriginalMetadataAutogen(String listFile, String version)
    throws FormatException, IOException
  {
    this.version = version;
    String[] files = DataTools.readFile(listFile).split("\n");
    for (int i=0; i<files.length; i++) {
      parseFile(files[i]);
      System.out.println("Parsed file #" + (i + 1) + " of " + files.length +
        " (" + ((i + 1) / ((double) files.length)) * 100 + "%)");
    }
  }

  // -- API Methods --

  public void write() throws Exception {
    VelocityEngine engine = VelocityTools.createEngine();
    VelocityContext context = VelocityTools.createContext();

    MetaSupportList supportList = new MetaSupportList(version);
    context.put("q", supportList);

    for (String format : metadata.keySet()) {
      supportList.setHandler(format);
      HashMap<String, ArrayList> meta = metadata.get(format);

      context.put("originalMetadata", meta);

      String filename = format.replaceAll(" ", "_");
      filename = filename.replaceAll("/", "_");

      VelocityTools.processTemplate(engine, context, TEMPLATE,
        "../../docs/sphinx/formats/" + filename + ".txt");
    }
  }

  // -- Helper methods --

  private void parseFile(String file) throws FormatException, IOException {
    try {
      reader.setId(file);
    }
    catch (Exception e) {
      return;
    }

    addMetadata(reader.getGlobalMetadata());
    for (int series=0; series<reader.getSeriesCount(); series++) {
      reader.setSeries(series);
      addMetadata(reader.getSeriesMetadata());
    }
    reader.close();
  }

  private void addMetadata(Hashtable<String, Object> readerMetadata) {
    String format = reader.getReader().getClass().getSimpleName();
    HashMap<String, ArrayList> meta = metadata.get(format);
    if (meta == null) {
      meta = new HashMap<String, ArrayList>();
    }
    for (String key : readerMetadata.keySet()) {
      String value = readerMetadata.get(key).toString();
      key = key.replaceAll("/", "\\/");
      key = key.replaceAll("\n", " ");
      key = key.replaceAll("\\p{Cntrl}", " ");
      if (key.startsWith("*")) {
        key = "\\" + key;
      }

      String[] lines = value.split("\n");
      if (lines.length == 0) {
        continue;
      }

      StringBuffer sb = new StringBuffer();
      sb.append(lines[0]);
      for (int i=1; i<lines.length; i++) {
        sb.append("\n      ");
        sb.append(lines[i].trim());
      }
      value = sb.toString();
      if (value.length() > 4095) {
        continue;
      }

      value = value.replaceAll("\\p{Cntrl}", " ");
      if (value.startsWith("*")) {
        value = "\\" + value;
      }

      if (meta.containsKey(key) && !meta.get(key).contains(value)) {
        meta.get(key).add(value);
      }
      else {
        ArrayList list = new ArrayList();
        list.add(value);
        meta.put(key, list);
      }
    }
    metadata.put(format, meta);
  }

  // -- Main method --

  public static void main(String[] args) throws Exception {
    OriginalMetadataAutogen autogen = new OriginalMetadataAutogen(args[0], args[1]);
    autogen.write();
  }

}
