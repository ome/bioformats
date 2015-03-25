/*
 * #%L
 * Bio-Formats autogen package for programmatically generating source code.
 * %%
 * Copyright (C) 2007 - 2015 Open Microscopy Environment:
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
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class OriginalMetadataAutogen {

  // -- Constants --

  private static final String TEMPLATE = "doc/OriginalMetadataSupport.vm";

  // -- Fields --

  private HashMap<String, HashMap<String, ArrayList>> metadata =
    new HashMap<String, HashMap<String, ArrayList>>();
  private ImageReader reader = new ImageReader();

  // -- Constructor --

  public OriginalMetadataAutogen(String listFile)
    throws FormatException, IOException
  {
    String[] files = DataTools.readFile(listFile).split("\n");
    for (int i=0; i<files.length; i++) {
      parseFile(files[i]);
      System.out.println("Parsed file #" + (i + 1) + " of " + files.length +
        " (" + ((i + 1) / ((double) files.length)) * 100 + "%)");
    }
  }

  // -- API Methods --

  public void write() throws Exception {
    File doc = new File("doc");
    if (!doc.exists()) {
      boolean success = doc.mkdir();
      if (!success) {
        throw new IOException("Could not create " + doc.getAbsolutePath());
      }
    }
    File docMeta = new File(doc, "original_meta");
    if (!docMeta.exists()) {
      boolean success = docMeta.mkdir();
      if (!success) {
        throw new IOException("Could not create " + docMeta.getAbsolutePath());
      }
    }

    VelocityEngine engine = VelocityTools.createEngine();
    VelocityContext context = VelocityTools.createContext();

    for (String format : metadata.keySet()) {
      HashMap<String, ArrayList> meta = metadata.get(format);

      context.put("q", meta);
      context.put("format", format);

      String filename = format.replaceAll(" ", "_");
      filename = filename.replaceAll("/", "_");

      VelocityTools.processTemplate(engine, context, TEMPLATE,
        "doc/original_meta/" + filename + ".txt");
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
    String format = reader.getFormat();
    HashMap<String, ArrayList> meta = metadata.get(format);
    if (meta == null) {
      meta = new HashMap<String, ArrayList>();
    }
    for (String key : readerMetadata.keySet()) {
      if (meta.containsKey(key) &&
        !meta.get(key).contains(readerMetadata.get(key)))
      {
        meta.get(key).add(readerMetadata.get(key));
      }
      else {
        ArrayList list = new ArrayList();
        list.add(readerMetadata.get(key));
        meta.put(key, list);
      }
    }
    metadata.put(format, meta);
  }

  // -- Main method --

  public static void main(String[] args) throws Exception {
    OriginalMetadataAutogen autogen = new OriginalMetadataAutogen(args[0]);
    autogen.write();
  }

}
