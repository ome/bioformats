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

  // -- Constants --

  /** Path to versions definition file. */
  public static final String VERSION_SRC = "MetadataAutogenVersions.txt";

  /** Path to entities definition file. */
  public static final String ENTITY_SRC = "MetadataAutogenEntities.txt";

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

    // parse versions file
    Hashtable versions = parseVersions();

    // parse entities file
    Vector entities = parseEntities();
    context.put("entities", entities);

    // generate base metadata classes
    processTemplate(ve, context,
      "MetadataStore.vm", "meta/MetadataStore.java");
    processTemplate(ve, context,
      "MetadataRetrieve.vm", "meta/MetadataRetrieve.java");
    processTemplate(ve, context,
      "DummyMetadata.vm", "meta/DummyMetadata.java");
    processTemplate(ve, context,
      "AggregateMetadata.vm", "meta/AggregateMetadata.java");

    // generate version-specific OME-XML metadata implementations
    Enumeration versionKeys = versions.keys();
    while (versionKeys.hasMoreElements()) {
      String versionKey = (String) versionKeys.nextElement();

      // first entity with each distinct path end node for this version
      HashSet nodes = new HashSet();
      Vector unique = new Vector();
      for (int i=0; i<entities.size(); i++) {
        Entity entity = (Entity) entities.get(i);
        String last = entity.last(versionKey);
        if (nodes.contains(last) || last.equals("-")) continue;
        nodes.add(last);
        unique.add(entity);
      }
      context.put("unique", unique);

      Hashtable vars = (Hashtable) versions.get(versionKey);
      // update context
      context.put("versionKey", versionKey);
      Enumeration varKeys = vars.keys();
      while (varKeys.hasMoreElements()) {
        String name = (String) varKeys.nextElement();
        String value = (String) vars.get(name);
        context.put(name, value);
      }
      processTemplate(ve, context,
        "OMEXMLMetadata.vm", "ome/OMEXML" + versionKey + "Metadata.java");
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

  private static Hashtable parseVersions() throws IOException {
    System.out.println("Reading " + VERSION_SRC + ":");
    Hashtable versions = new Hashtable();

    String versionKey = null;
    Hashtable vars = null;
    BufferedReader in = new BufferedReader(new InputStreamReader(
      MetadataAutogen.class.getResourceAsStream(VERSION_SRC)));
    while (true) {
      String line = in.readLine();
      if (line == null) break;
      line = line.trim();

      if (line.startsWith("#")) continue; // comment
      if (line.equals("")) continue; // blank line

      if (line.startsWith("[")) {
        // version header
        if (versionKey != null) versions.put(versionKey, vars);
        if (line.startsWith("[-")) break;
        versionKey = line.substring(1, line.length() - 1);
        vars = new Hashtable();
        System.out.println("\t" + versionKey);
        continue;
      }
      int equals = line.indexOf("=");
      String key = line.substring(0, equals).trim();
      String val = line.substring(equals + 1).trim();
      vars.put(key, val);
    }
    in.close();
    return versions;
  }

  public static Vector parseEntities() throws IOException {
    System.out.println("Reading " + ENTITY_SRC + ":");
    Vector entities = new Vector();
    Entity entity = null;
    Property prop = null;
    BufferedReader in = new BufferedReader(new InputStreamReader(
      MetadataAutogen.class.getResourceAsStream(ENTITY_SRC)));
    while (true) {
      String line = in.readLine();
      if (line == null) break;
      line = line.trim();

      if (line.startsWith("#")) continue; // comment
      if (line.equals("")) continue; // blank line

      if (line.startsWith("[")) {
        // entity header
        if (entity != null) entities.add(entity);
        if (line.startsWith("[-")) break;
        String name = line.substring(1, line.length() - 1);
        String desc = in.readLine();
        String extra = in.readLine();
        entity = new Entity(name, desc, extra);
        System.out.println("\t" + entity.name());
        continue;
      }

      if (line.startsWith("*")) {
        int colon = line.indexOf(":");
        if (colon < 0) {
          System.err.println("Warning: invalid line: " + line);
          continue;
        }
        String versionKey = line.substring(1, colon).trim();
        String propName = line.substring(colon + 1).trim();
        prop.addMappedName(versionKey, propName);
        continue;
      }

      int colon = line.indexOf(":");
      if (colon >= 0) {
        // path to XML element for a particular schema version
        String versionKey = line.substring(0, colon).trim();
        String path = line.substring(colon + 1).trim();
        entity.addPath(versionKey, path);
        continue;
      }

      StringTokenizer st = new StringTokenizer(line);
      int tokens = st.countTokens();
      if (tokens < 2) {
        // unknown line type
        System.err.println("Warning: invalid line: " + line);
        continue;
      }

      // property
      String propType = st.nextToken();
      String propName = st.nextToken();
      StringBuffer sb = new StringBuffer();
      if (st.hasMoreTokens()) sb.append(st.nextToken());
      while (st.hasMoreTokens()) {
        sb.append(" ");
        sb.append(st.nextToken());
      }
      String propDoc = sb.toString();
      prop = new Property(propName, propType, propDoc, entity, false);
    }
    in.close();
    return entities;
  }

}
