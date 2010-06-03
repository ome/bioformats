//
// MetadataAutogen.java
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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * Automatically generates code for the MetadataStore and MetadataRetrieve
 * interfaces, as well as the implementations for various flavors of OME-XML.
 *
 * Uses data from the entities.txt and versions.txt files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/auto/MetadataAutogen.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/auto/MetadataAutogen.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class MetadataAutogen {

  // -- Main method --

  public static void main(String[] args) throws Exception {
    boolean ice = false;
    for (int i=0; i<args.length; i++) {
      if (args[i].equals("-ice")) ice = true;
    }

    // create needed directories
    if (ice) {
      File lociDir = new File("loci");
      if (!lociDir.exists()) {
        boolean success = lociDir.mkdir();
        if (!success) {
          throw new IOException("Could not create " +
            lociDir.getAbsolutePath());
        }
      }
      File iceDir = new File(lociDir, "ice");
      if (!iceDir.exists()) {
        boolean success = iceDir.mkdir();
        if (!success) {
          throw new IOException("Could not create " + iceDir.getAbsolutePath());
        }
      }
      File formatsDir = new File(iceDir, "formats");
      if (!formatsDir.exists()) {
        boolean success = formatsDir.mkdir();
        if (!success) {
          throw new IOException("Could not create " +
            formatsDir.getAbsolutePath());
        }
      }
    }
    else {
      File omeDir = new File("ome");
      if (!omeDir.exists()) {
        boolean success = omeDir.mkdir();
        if (!success) {
          throw new IOException("Could not create " + omeDir.getAbsolutePath());
        }
      }
      File metaDir = new File("meta");
      if (!metaDir.exists()) {
        boolean success = metaDir.mkdir();
        if (!success) {
          throw new IOException("Could not create " +
            metaDir.getAbsolutePath());
        }
      }
    }

    // get Velocity objects
    VelocityEngine ve = VelocityTools.createEngine();
    VelocityContext context = VelocityTools.createContext();

    // parse entity list
    MetaEntityList entityList = new MetaEntityList();
    context.put("q", entityList);

    if (ice) {
      // generate Slice interfaces
      VelocityTools.processTemplate(ve, context,
        "ice/bio-formats.vm", "bio-formats.ice");

      // generate server-side Ice implementations
      VelocityTools.processTemplate(ve, context,
        "ice/IMetadataI.vm", "loci/ice/formats/IMetadataI.java");
    }
    else {
      // generate base metadata classes
      VelocityTools.processTemplate(ve, context,
        "meta/MetadataStore.vm", "meta/MetadataStore.java");
      VelocityTools.processTemplate(ve, context,
        "meta/MetadataRetrieve.vm", "meta/MetadataRetrieve.java");
      VelocityTools.processTemplate(ve, context,
        "meta/DummyMetadata.vm", "meta/DummyMetadata.java");
      VelocityTools.processTemplate(ve, context,
        "meta/FilterMetadata.vm", "meta/FilterMetadata.java");
      VelocityTools.processTemplate(ve, context,
        "meta/AggregateMetadata.vm", "meta/AggregateMetadata.java");
      context.put("convertMetadataBody", generateConvertMetadata(entityList));
      VelocityTools.processTemplate(ve, context,
        "meta/MetadataConverter.vm", "meta/MetadataConverter.java");

      // NB : these IMetadata implementations are no longer necessary
      /*
      // generate version-specific OME-XML metadata implementations
      for (String versionKey : entityList.versions()) {
        entityList.setVersion(versionKey);
        VelocityTools.processTemplate(ve, context, "ome/OMEXMLMetadata.vm",
          "ome/OMEXML" + versionKey + "Metadata.java");
      }
      */
    }
  }

  // -- Helper methods --

  private static String generateConvertMetadata(final MetaEntityList q) {
    System.out.println("Generating convertMetadata body");
    StringWriter sw = new StringWriter();
    PrintWriter out = new PrintWriter(sw);
    final int indent = 2;

    Vector<String> entities = q.entities();

    // sort entities by path
    Collections.sort(entities, new Comparator<String>() {
      public int compare(String e1, String e2) {
        q.setEntity(e1);
        String path1 = q.path();
        q.setEntity(e2);
        String path2 = q.path();
        return path1.compareTo(path2);
      }
    });

    StringBuffer spaces = new StringBuffer();
    Vector<String> lastIndices = new Vector<String>();

    int maxLen = 0;
    for (String entity : entities) {
      int len = entity.length();
      if (len > maxLen) maxLen = len;
    }

    for (String entity : entities) {
      q.setEntity(entity);
      System.out.print("\t");
      System.out.print(entity);
      for (int i=entity.length(); i<maxLen; i++) System.out.print(" ");
      System.out.println(" | " + q.path());

      Vector<String> indices = q.indices();

      // find deepest common element
      int depth = 0;

      for (int j=0; j<indices.size(); j++) {
        if (j >= lastIndices.size()) break;
        String lastIndex = lastIndices.get(j);
        String thisIndex = indices.get(j);
        if (!lastIndex.equals(thisIndex)) break;
        depth++;
      }

      // end old for loops
      int lastDepth = lastIndices.size();
      for (int j=lastDepth; j>depth; j--) {
        spaces.setLength(0);
        for (int k=1; k<j+indent; k++) spaces.append("  ");
        out.println(spaces + "}");
        out.println(spaces + "} catch (NullPointerException e) { }");
      }

      // start new for loops
      for (int j=depth; j<indices.size(); j++) {
        String index = indices.get(j);
        spaces.setLength(0);
        for (int k=0; k<j+indent; k++) spaces.append("  ");
        String iVar = q.var(index + "Index");
        String countVar = iVar.replaceFirst("Index$", "Count");
        out.println(spaces + "try {");
        out.println(spaces + "int " + countVar + " = " +
          "src.get" + index + "Count" + "(" +
          q.varsList(q.chop(), q.chop(q.defaultPath())) + ");");
        out.println(spaces + "for (int " + iVar + "=0; " +
          iVar + "<" + countVar + "; " + iVar + "++) {");
      }
      lastIndices = indices;

      // set properties
      Vector<String> props = q.props();
      for (String prop : props) {
        q.setProperty(prop);
        String methodName = entity + prop;
        String iList = q.varsList();
        String type = q.defaultType();
        String var = q.var(methodName) + "Value";
        out.println(spaces + "try {");
        out.println(spaces + "  " + type + " " + var + " = " +
          "src.get" + methodName + "(" + iList + ");");
        out.println(spaces + "  if (" + var + " != null) " +
          "dest.set" + methodName + "(" + var + ", " + iList + ");");
        out.println(spaces + "} catch (NullPointerException e) { }");
      }
    }

    // end remaining for loops
    int lastDepth = lastIndices.size();
    for (int j=lastDepth; j>0; j--) {
      spaces.setLength(0);
      for (int k=1; k<j+indent; k++) spaces.append("  ");
      out.println(spaces + "}");
      out.println(spaces + "} catch (NullPointerException e) { }");
    }

    out.close();
    return sw.toString();
  }

}
