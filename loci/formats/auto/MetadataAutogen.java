//
// MetadataAutogen.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
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
 * Automatically generates code for the MetadataStore and MetadataRetrieve
 * interfaces, as well as the implementations for various flavors of OME-XML.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/auto/MetadataAutogen.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/auto/MetadataAutogen.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class MetadataAutogen {

  // -- Main method --

  public static void main(String[] args) throws Exception {
    // create needed directories
    File ome = new File("ome");
    if (!ome.exists()) ome.mkdir();
    File meta = new File("meta");
    if (!meta.exists()) meta.mkdir();

    // initialize Velocity engine; enable loading of templates as resources
    VelocityEngine ve = new VelocityEngine();
    Properties p = new Properties();
    p.setProperty("resource.loader", "class");
    p.setProperty("class.resource.loader.class",
      "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    ve.init(p);

    // populate Velocity context
    VelocityContext context = new VelocityContext();
    context.put("user", System.getProperty("user.name"));
    DateFormat dateFmt = DateFormat.getDateInstance(DateFormat.MEDIUM);
    DateFormat timeFmt = DateFormat.getTimeInstance(DateFormat.LONG);
    Date date = Calendar.getInstance().getTime();
    context.put("timestamp", dateFmt.format(date) + " " + timeFmt.format(date));

    // parse entity list
    MetaEntityList entityList = new MetaEntityList();
    context.put("q", entityList);

    // generate base metadata classes
    processTemplate(ve, context,
      "MetadataStore.vm", "meta/MetadataStore.java");
    processTemplate(ve, context,
      "MetadataRetrieve.vm", "meta/MetadataRetrieve.java");
    processTemplate(ve, context,
      "DummyMetadata.vm", "meta/DummyMetadata.java");
    processTemplate(ve, context,
      "FilterMetadata.vm", "meta/FilterMetadata.java");
    processTemplate(ve, context,
      "AggregateMetadata.vm", "meta/AggregateMetadata.java");
    context.put("convertMetadataBody", generateConvertMetadata(entityList));
    processTemplate(ve, context,
      "MetadataConverter.vm", "meta/MetadataConverter.java");

    // generate version-specific OME-XML metadata implementations
    for (String versionKey : entityList.versions()) {
      entityList.setVersion(versionKey);
      processTemplate(ve, context, "OMEXMLMetadata.vm",
        "ome/OMEXML" + versionKey + "Metadata.java");
    }
  }

  // -- Helper methods --

  private static void processTemplate(VelocityEngine ve,
    VelocityContext context, String inFile, String outFile)
    // NB: No choice, as VelocityEngine.getTemplate(String) throws Exception
    throws Exception
  {
    System.out.print("Writing " + outFile + ": ");
    Template t = ve.getTemplate("loci/formats/auto/" + inFile);
    StringWriter writer = new StringWriter();
    t.merge(context, writer);
    PrintWriter out = new PrintWriter(new FileWriter(outFile));
    out.print(writer.toString());
    out.close();
    System.out.println("done.");
  }

  private static String generateConvertMetadata(MetaEntityList q) {
    StringWriter sw = new StringWriter();
    PrintWriter out = new PrintWriter(sw);
    final int indent = 2;

    Vector<String> entities = q.entities();

    StringBuffer spaces = new StringBuffer();
    Vector<String> lastIndices = new Vector<String>();

    for (String entity : entities) {
      q.setEntity(entity);

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
      }

      // start new for loops
      for (int j=depth; j<indices.size(); j++) {
        String index = indices.get(j);
        spaces.setLength(0);
        for (int k=0; k<j+indent; k++) spaces.append("  ");
        String iVar = q.var(index + "Index");
        String countVar = iVar.replaceFirst("Index$", "Count");
        out.println(spaces + "int " + countVar + " = " +
          "src.get" + index + "Count" + "(" +
          q.indicesList(false, true, false) + ");");
        out.println(spaces + "for (int " + iVar + "=0; " +
          iVar + "<" + countVar + "; " + iVar + "++) {");
      }
      lastIndices = indices;

      // set properties
      Vector<String> props = q.props();
      for (String prop : props) {
        String methodName = entity + prop;
        String iList = q.indicesList(false, true);
        out.println(spaces + "  dest.set" + methodName +
          "(src.get" + methodName + "(" + iList + "), " + iList + ");");
      }
    }

    // end remaining for loops
    int lastDepth = lastIndices.size();
    for (int j=lastDepth; j>0; j--) {
      spaces.setLength(0);
      for (int k=1; k<j+indent; k++) spaces.append("  ");
      out.println(spaces + "}");
    }

    out.close();
    return sw.toString();
  }

}
