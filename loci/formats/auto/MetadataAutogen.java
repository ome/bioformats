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

  public static final String STORE_SRC = "MetadataAutogenHeaderStore.txt";
  public static final String RETRIEVE_SRC = "MetadataAutogenHeaderRetrieve.txt";
  public static final String OMEXML_SRC = "MetadataAutogenHeaderOMEXML.txt";
  public static final String GLOBAL_SRC = "MetadataAutogenGlobal.txt";
  public static final String NODES_SRC = "MetadataAutogenNodes.txt";

  // -- Static fields --

  private static Hashtable versions;
  private static Hashtable basicVars;
  private static String storeHeader;
  private static String retrieveHeader;
  private static String omexmlHeader;
  private static OutFiles out = new OutFiles();

  // -- Main method --

  public static void main(String[] args) throws IOException {
    System.out.print("Parsing input files... ");
    File ome = new File("ome");
    if (!ome.exists()) ome.mkdir();

    // parse global input file
    versions = parseGlobal();

    // parse header input files
    storeHeader = parseHeader(STORE_SRC);
    retrieveHeader = parseHeader(RETRIEVE_SRC);
    omexmlHeader = parseHeader(OMEXML_SRC);

    // parse nodes input file
    Vector nodes = parseNodes();

    System.out.println("done.");
    System.out.println("Generating classes...");

    // generate classes
    for (int i=0; i<nodes.size(); i++) {
      Node node = (Node) nodes.get(i);
      doGetters(node);
    }
    for (int i=0; i<nodes.size(); i++) {
      Node node = (Node) nodes.get(i);
      doSetters(node);
    }
    for (int i=0; i<nodes.size(); i++) {
      Node node = (Node) nodes.get(i);
      doHelpers(node);
    }
    out.closeFiles();

    System.out.println("Autogeneration complete.");
  }

  // -- Helper methods --

  private static Hashtable parseGlobal() throws IOException {
    Hashtable hash = new Hashtable();
    String user = System.getProperty("user.name");
    DateFormat dateFmt = DateFormat.getDateInstance(DateFormat.MEDIUM);
    DateFormat timeFmt = DateFormat.getTimeInstance(DateFormat.LONG);
    Date date = Calendar.getInstance().getTime();
    String timestamp = dateFmt.format(date) + " " + timeFmt.format(date);

    basicVars = new Hashtable();
    basicVars.put("user", user);
    basicVars.put("timestamp", timestamp);

    String version = null;
    Hashtable vars = null;
    BufferedReader in = new BufferedReader(new InputStreamReader(
      MetadataAutogen.class.getResourceAsStream(GLOBAL_SRC)));
    while (true) {
      String line = in.readLine();
      if (line == null) break;
      line = line.trim();

      if (line.startsWith("#")) continue; // comment
      if (line.equals("")) continue; // blank line

      if (line.startsWith("[")) {
        // version header
        if (version != null) hash.put(version, vars);
        if (line.startsWith("[-")) break;
        version = line.substring(1, line.length() - 1);
        vars = new Hashtable();
        vars.put("user", user);
        vars.put("timestamp", timestamp);
        continue;
      }
      int equals = line.indexOf("=");
      String key = line.substring(0, equals).trim();
      String val = line.substring(equals + 1).trim();
      vars.put(key, val);
    }
    in.close();
    return hash;
  }

  private static String parseHeader(String id) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(
      MetadataAutogen.class.getResourceAsStream(id)));
    StringBuffer sb = new StringBuffer();
    while (true) {
      String line = in.readLine();
      if (line == null) break;
      sb.append(line);
      sb.append("\n");
    }
    in.close();
    return sb.toString();
  }

  private static Vector parseNodes() throws IOException {
    Vector nodes = new Vector();
    Node node = null;
    BufferedReader in = new BufferedReader(new InputStreamReader(
      MetadataAutogen.class.getResourceAsStream(NODES_SRC)));
    while (true) {
      String line = in.readLine();
      if (line == null) break;
      line = line.trim();

      if (line.startsWith("#")) continue; // comment
      if (line.equals("")) continue; // blank line

      if (line.startsWith("[")) {
        // node header
        if (node != null) nodes.add(node);
        if (line.startsWith("[-")) break;
        node = new Node();
        node.name = line.substring(1, line.length() - 1);
        node.desc = in.readLine();
        continue;
      }

      int colon = line.indexOf(":");
      if (colon >= 0) {
        // path to XML element for a particular schema version
        String version = line.substring(0, colon).trim();
        String path = line.substring(colon + 1).trim();
        node.paths.put(version, path);
        continue;
      }

      StringTokenizer st = new StringTokenizer(line);
      int tokens = st.countTokens();
      if (tokens < 2) {
        // unknown line type
        System.err.println("Warning: invalid line: " + line);
        continue;
      }

      // parameter
      Param param = new Param();
      param.type = st.nextToken();
      param.name = st.nextToken();
      StringBuffer sb = new StringBuffer();
      if (st.hasMoreTokens()) sb.append(st.nextToken());
      while (st.hasMoreTokens()) {
        sb.append(" ");
        sb.append(st.nextToken());
      }
      param.doc = sb.toString();
      node.params.add(param);
    }
    in.close();
    return nodes;
  }

  /** Generates getter methods for each output source file. */
  private static void doGetters(Node node) throws IOException {
    boolean first = true;
    Enumeration e = node.paths.keys();
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      String value = (String) node.paths.get(key);
      if (first) {
        // MetadataRetrieve interface
        doGetters("MetadataRetrieve.java", value, node, true);
        first = false;
      }
      // OME-XML implementations
      doGetters("ome/OMEXML" + key + "Metadata.java", value, node, false);
    }
  }

  /** Generates setter methods for each output source file. */
  private static void doSetters(Node node) throws IOException {
    boolean first = true;
    Enumeration e = node.paths.keys();
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      String value = (String) node.paths.get(key);
      if (first) {
        // MetadataStore interface
        doSetters("MetadataStore.java", value, node, true);
        first = false;
      }
      // OME-XML implementation
      doSetters("ome/OMEXML" + key + "Metadata.java", value, node, false);
    }
  }

  /** Generates helper methods for each output source file. */
  private static void doHelpers(Node node) throws IOException {
    Enumeration e = node.paths.keys();
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      String value = (String) node.paths.get(key);
      // OME-XML implementation
      doHelpers("ome/OMEXML" + key + "Metadata.java", value, node);
    }
  }

  /** Generates getter methods for the given node and source file. */
  private static void doGetters(String id, String path, Node node,
    boolean iface) throws IOException
  {
    String end = iface ? ");" : ")";
    LineTracker lt = new LineTracker();

    // parse path
    Vector indices = getIndices(path);
    int psize = node.params.size();
    int isize = indices.size();

    lt.add("  // -- " + node.name + " attribute retrieval methods --");
    lt.newline();
    lt.newline();

    for (int i=0; i<psize; i++) {
      Param pi = (Param) node.params.get(i);
      if (i > 0) lt.newline();

      // javadoc
      if (iface) {
        lt.add("  /**");
        lt.newline();
        lt.add("   * Gets ");
        lt.addTokens(pi.doc + " for a particular " + node.name + ".", "   * ");
        lt.newline();
        for (int j=0; j<isize; j++) {
          Param pj = (Param) indices.get(j);
          lt.add("   * @param " + toVarName(pj.name) + " ");
          lt.addTokens(pj.doc + ".", "   *   ");
          lt.newline();
        }
        lt.add("   */");
        lt.newline();
      }
      else {
        StringBuffer sb = new StringBuffer();
        sb.append("@see loci.formats.MetadataRetrieve#get" +
          node.name + pi.name + "(");
        for (int j=0; j<isize; j++) {
          Param pj = (Param) indices.get(j);
          sb.append(pj.type);
          sb.append(j < isize - 1 ? ", " : ")");
        }
        if (sb.length() <= 72) {
          lt.add("  /* " + sb.toString() + " */");
          lt.newline();
        }
        else {
          lt.add("  /*");
          lt.newline();
          lt.add("   * ");
          lt.addTokens(sb.toString(), "   *   ");
          lt.newline();
          lt.add("   */");
          lt.newline();
        }
      }
      // method signature
      lt.add("  ");
      if (!iface) lt.add("public ");
      lt.add(pi.type + " get" + node.name + pi.name + "(");
      for (int j=0; j<isize; j++) {
        // parameters
        Param pj = (Param) indices.get(j);
        lt.add(pj.getArg(true, true, j == 0, j == isize - 1, end));
      }
      if (!iface) {
        // opening brace
        if (lt.hasWrapped()) {
          lt.newline();
          lt.add(" ");
        }
        lt.add(" {", "  ");
      }
      lt.newline();
      // method body
      if (!iface) {
        lt.add("    // TODO");
        // closing brace
        lt.newline();
        lt.add("    return null;");
        lt.newline();
        lt.add("  }");
        lt.newline();
      }
    }

    // output results
    out.write(id, lt.toString());
  }

  /** Generates setter methods for the given node and source file. */
  private static void doSetters(String id, String path, Node node,
    boolean iface) throws IOException
  {
    String end = iface ? ");" : ")";
    LineTracker lt = new LineTracker();

    // parse path
    Vector indices = getIndices(path);
    int psize = node.params.size();
    int isize = indices.size();
    int total = psize + isize;

    // javadoc
    if (iface) {
      lt.add("  /**");
      lt.newline();
      lt.add("   * Sets ");
      lt.addTokens(node.desc + ".", "   * ");
      lt.newline();
      for (int i=0; i<total; i++) {
        Param p = (Param)
          (i < psize ? node.params.get(i) : indices.get(i - psize));
        lt.add("   * @param " + toVarName(p.name) + " ");
        lt.addTokens(p.doc + ".", "   *   ");
        lt.newline();
      }
      lt.add("   */");
      lt.newline();
    }
    else {
      StringBuffer sb = new StringBuffer();
      sb.append("@see loci.formats.MetadataStore#set" + node.name + "(");
      for (int i=0; i<total; i++) {
        Param p = (Param)
          (i < psize ? node.params.get(i) : indices.get(i - psize));
        sb.append(p.type);
        sb.append(i < total - 1 ? ", " : ")");
      }
      if (sb.length() <= 72) {
        lt.add("  /* " + sb.toString() + " */");
        lt.newline();
      }
      else {
        lt.add("  /*");
        lt.newline();
        lt.add("   * ");
        lt.addTokens(sb.toString(), "   *   ");
        lt.newline();
        lt.add("   */");
        lt.newline();
      }
    }
    // method signature
    lt.add("  ");
    if (!iface) lt.add("public ");
    lt.add("void set" + node.name + "(");
    for (int i=0; i<total; i++) {
      // parameters
      Param p = (Param)
        (i < psize ? node.params.get(i) : indices.get(i - psize));
      lt.add(p.getArg(true, true, i == 0, i == total - 1, end), "    ");
    }
    if (!iface) {
      // opening brace
      if (lt.hasWrapped()) {
        lt.newline();
        lt.add(" ");
      }
      lt.add(" {", "  ");
    }
    lt.newline();
    // method body
    if (!iface) {
      String varName = toVarName(node.name);
      lt.add("    " + node.name + "Node " + varName +
        " = get" + node.name + "(");
      for (int i=0; i<isize; i++) {
        Param p = (Param) indices.get(i);
        lt.add(p.getArg(true, false, i == 0, false, null), "    ");
      }
      lt.add(" true);", "    ");
      lt.newline();
      for (int i=0; i<psize; i++) {
        Param p = (Param) node.params.get(i);
        lt.add("    if (" + toVarName(p.name) + " != null) {");
        lt.newline();
        lt.add("      " + varName +
          ".set" + p.name + "(" + toVarName(p.name) + ");");
        lt.newline();
        lt.add("    }");
        lt.newline();
      }
      // closing brace
      lt.newline();
      lt.add("  }");
      lt.newline();
    }
    // output results
    out.write(id, lt.toString());
  }

  /** Generates helper methods for the given node and source file. */
  private static void doHelpers(String id, String path, Node node)
    throws IOException
  {
    String end = ")";
    LineTracker lt = new LineTracker();

    // parse path
    Vector indices = getIndices(path);
    int psize = node.params.size();
    int isize = indices.size();

    // method signature
    lt.add("  // " + path);
    lt.newline();
    lt.add("  private " + node.name + "Node get" + node.name + "(");
    for (int i=0; i<isize; i++) {
      // parameters
      StringBuffer sb = new StringBuffer();
      if (i > 0) sb.append(" ");
      Param p = (Param) indices.get(i);
      sb.append(p.type);
      sb.append(" ");
      sb.append(toVarName(p.name));
      sb.append(",");
      lt.add(sb.toString(), "    ");
    }
    lt.add(" boolean create)", "    ");
    // opening brace
    if (lt.hasWrapped()) {
      lt.newline();
      lt.add(" ");
    }
    lt.add(" {", "  ");
    lt.newline();
    // method body
    int n = 0;
    lt.add("    List list;");
    lt.newline();
    lt.add("    int ndx, count;");
    lt.newline();
    StringTokenizer st = new StringTokenizer(path, "/");
    lt.add("    // get OME node");
    lt.newline();
    lt.add("    OMENode ome = (OMENode) root;");
    lt.newline();
    String var = "ome";
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      boolean multi = false;
      if (token.endsWith("+")) {
        multi = true;
        token = token.substring(0, token.length() - 1);
      }
      lt.add("    // get " + token + " node");
      lt.newline();
      String varToken = toVarName(token);
      String nextVar = token.equals("CustomAttributes") ? "ca" : varToken;
      if (multi) {
        lt.add("    ndx = i2i(" + varToken + "Index);");
        lt.newline();
        lt.add("    count = " + var + ".count" + token + "s();");
        lt.newline();
        lt.add("    if (!create && ndx >= count) return null;");
        lt.newline();
        lt.add("    for (int i=count; i<=ndx; i++) " +
           "new " + token + "Node(" + var + ");");
        lt.newline();
        lt.add("    list = " + var + ".get" + token + "s();");
        lt.newline();
        lt.add("    " + token + "Node " + nextVar +
          " = (" + token + "Node) list.get(ndx);");
        lt.newline();
      }
      else {
        lt.add("    " + token + "Node " + nextVar +
          " = " + var + ".get" + token + "();");
        lt.newline();
        lt.add("    if (" + nextVar + " == null) {");
        lt.newline();
        lt.add("      if (!create) return null;");
        lt.newline();
        lt.add("      " + nextVar + " = new " + token + "Node(" + var + ");");
        lt.newline();
        lt.add("    }");
        lt.newline();
      }
      var = nextVar;
    }
    lt.add("    return " + var + ";");
    lt.newline();
    // closing brace
    lt.add("  }");
    lt.newline();
    // output results
    out.write(id, lt.toString());
  }

  /** Converts attribute name in CamelCase to variable in variableCase. */
  private static String toVarName(String attr) {
    char[] c = attr.toCharArray();
    for (int i=0; i<c.length; i++) {
      if (c[i] >= 'A' && c[i] <= 'Z') c[i] += 'a' - 'A';
      else break;
    }
    return new String(c);
  }

  /** Gets parameter list corresponding to needed indices for a node. */
  private static Vector getIndices(String path) {
    Vector indices = new Vector();
    if (path != null) {
      StringTokenizer st = new StringTokenizer(path, "/");
      int tokens = st.countTokens();
      for (int i=0; i<tokens; i++) {
        String t = st.nextToken();
        if (t.endsWith("+")) {
          Param p = new Param();
          t = t.substring(0, t.length() - 1);
          p.name = toVarName(t) + "Index";
          p.type = "Integer";
          p.doc = "index of the " + t;
          indices.add(p);
        }
      }
    }
    return indices;
  }

  /** Writes header for the given output source file. */
  private static void writeHeader(String id) throws IOException {
    System.out.println(id);

    String header = null;
    Hashtable vars = null;
    if (id.startsWith("MetadataStore")) {
      header = storeHeader;
      vars = basicVars;
    }
    else if (id.startsWith("MetadataRetrieve")) {
      header = retrieveHeader;
      vars = basicVars;
    }
    else { // id.startsWith("ome/OMEXML")
      header = omexmlHeader;
      vars = (Hashtable) versions.get(id.substring(10, id.length() - 13));
    }
    header = filterHeader(header, vars);
    out.write(id, header);
  }

  /** Writes footer for the given output source file. */
  private static void writeFooter(String id) throws IOException {
    out.write(id, "}");
  }

  /**
   * Filters tokens for the given header, filling in
   * the values from the specified hashtable.
   */
  private static String filterHeader(String header, Hashtable vars) {
    Enumeration e = vars.keys();
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      String val = (String) vars.get(key);
      header = header.replaceAll("\\$\\{" + key + "\\}", val);
    }
    return header;
  }

  // -- Helper classes --

  /** A helper class for storing information about a node. */
  private static class Node {
    private String name = null, desc = null;
    private Hashtable paths = new Hashtable();
    private Vector params = new Vector();
  }

  /** A helper class for storing information about node parameters. */
  private static class Param {
    private String name, type, doc;
    public String getArg(boolean doName, boolean doType,
      boolean first, boolean last, String end)
    {
      StringBuffer sb = new StringBuffer();
      if (!first) sb.append(" ");
      if (doType) sb.append(type);
      if (doType && doName) sb.append(" ");
      if (doName) sb.append(toVarName(name));
      sb.append(last ? end : ",");
      return sb.toString();
    }
  }

  /** A helper class for managing line output with intelligent wrapping. */
  public static class LineTracker {
    private static final int MAX_LEN = 80;
    private Vector lines = new Vector();
    private StringBuffer line = new StringBuffer();
    private boolean wrapped = false;
    public void newline() {
      lines.add(line.toString());
      line.setLength(0);
      wrapped = false;
    }
    public void add(String s) { add(s, ""); }
    public void add(String s, String lead) {
      if (line.length() + s.length() > MAX_LEN) {
        newline();
        wrapped = true;
        s = lead + s.trim();
      }
      line.append(s);
    }
    public void addTokens(String s, String lead) {
      StringTokenizer st = new StringTokenizer(s);
      if (st.hasMoreTokens()) add(st.nextToken());
      while (st.hasMoreTokens()) add(" " + st.nextToken(), lead);
    }
    public boolean hasWrapped() { return wrapped; }
    public String toString() {
      StringBuffer sb = new StringBuffer();
      for (int i=0; i<lines.size(); i++) sb.append(lines.get(i) + "\n");
      if (line.length() > 0) sb.append(line);
      return sb.toString();
    }
  }

  /** A helper class for managing open output files. */
  public static class OutFiles {
    private Hashtable files = new Hashtable();
    public void write(String id, String s) throws IOException {
      getWriter(id).println(s);
    }
    public void closeFiles() throws IOException {
      Enumeration e = files.keys();
      while (e.hasMoreElements()) {
        String id = (String) e.nextElement();
        PrintWriter out = (PrintWriter) files.get(id);
        writeFooter(id);
        out.close();
      }
      files.clear();
    }
    private void openFile(String id) throws IOException {
      PrintWriter out = new PrintWriter(new FileWriter(id));
      files.put(id, out);
      writeHeader(id);
    }
    private PrintWriter getWriter(String id) throws IOException {
      if (files.get(id) == null) openFile(id);
      return (PrintWriter) files.get(id);
    }
  }

}
