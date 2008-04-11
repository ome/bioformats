//
// MetaEntityList.java
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
import java.util.*;

/**
 * An entity list for the OME data model.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/auto/MetaEntityList.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/auto/MetaEntityList.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class MetaEntityList extends EntityList {

  // -- Constants --

  /** Path to versions definition file. */
  public static final String VERSION_SRC = "versions.txt";

  /** Path to entities definition file. */
  public static final String ENTITY_SRC = "entities.txt";

  // -- Constructors --

  /** Constructs an entity list for the OME data model. */
  public MetaEntityList() throws IOException {
    super(VERSION_SRC, ENTITY_SRC);
  }

  // -- MetaEntityList API methods - versions --

  public String className() { return value("className"); }

  public String id() { return value("id"); }

  public String basePackage() { return value("basePackage"); }

  public String subPackage() { return value("subPackage"); }

  public String version() { return value("version"); }

  public boolean legacy() { return "true".equals(value("legacy")); }

  // -- MetaEntityList API methods - entities --

  /** Whether the entity can appear multiple times. */
  public boolean countable() { return "true".equals(value("countable")); }

  public String path() { return value("path"); }

  /** List of nodes in the path, with markup symbols. Derived from path. */
  public String[] pathNodes() { return path().split("\\/"); }

  /** Last node in the path, without markup symbols. Derived from path. */
  public String last() {
    String path = path();
    int first = path.lastIndexOf("/") + 1;
    return path.substring(first).replaceAll("[@\\!\\+]", "");
  }

  /** List of indices in the path. Derived from path. */
  public Vector<String> indices() {
    Vector<String> list = new Vector<String>();
    StringTokenizer st = new StringTokenizer(path(), "/");
    int tokens = st.countTokens();
    for (int i=0; i<tokens; i++) {
      String t = st.nextToken();
      if (t.endsWith("+")) list.add(t.replaceAll("[@\\!\\+]", ""));
    }
    return list;
  }

  public String indicesList(boolean doTypes, boolean doVars) {
    return indicesList(doTypes, doVars, true);
  }

  public String indicesList(boolean doTypes, boolean doVars, boolean doLast) {
    StringBuffer sb = new StringBuffer();
    Vector<String> indices = indices();
    if (!doLast) indices.remove(indices.size() - 1); // ignore last element
    boolean first = true;
    for (String index : indices) {
      if (first) first = false;
      else sb.append(", ");
      if (doTypes) sb.append("int");
      if (doTypes && doVars) sb.append(" ");
      if (doVars) sb.append(var(index + "Index"));
    }
    return sb.toString();
  }

  /**
   * List of distinct path values for the active version.
   * Derived from path values of all entities and properties.
   */
  public Vector<String> unique() {
    HashSet<String> set = new HashSet<String>();
    Vector<String> unique = new Vector<String>();
    for (String entity : entities.keySet()) {
      Entity e = entities.get(entity);
      for (String property : e.props.keySet()) {
        String path = value("path", ver, entity, property);
        if (set.contains(path) || path.equals("-")) continue;
        set.add(path);
        unique.add(path);
      }
    }
    return unique;
  }

  // -- MetaEntityList API methods - properties --

  public String type() { return value("type"); }

  public String getter() {
    String getter = value("getter");
    return getter == null ? "get" + name() : getter;
  }

  public String setter() {
    String setter = value("setter");
    return setter == null ? "set" + name() : setter;
  }

  public boolean available() { return !"false".equals(value("available")); }

  // -- MetaEntityList API methods - entities/properties --

  public String description() { return value("description"); }

  public String notes() { return value("notes"); }

  /** Synthesized combination of description and notes. */
  public String doc() {
    StringBuffer sb = new StringBuffer();
    sb.append(description());
    sb.append(".");
    String notes = notes();
    if (notes != null) {
      sb.append(" ");
      sb.append(notes);
    }
    return sb.toString();
  }

  // -- MetaEntityList API methods - other --

  /** Converts name in CamelCase to variable in variableCase. */
  public String var(String s) {
    // NB: This method could be static, but is an instance method
    // to make it easier for Velocity templates to reference it.
    char[] c = s.toCharArray();
    for (int i=0; i<c.length; i++) {
      if (c[i] >= 'A' && c[i] <= 'Z') c[i] += 'a' - 'A';
      else {
        if (i > 1) c[i - 1] += 'A' - 'a'; // keep last character capitalized
        break;
      }
    }
    return new String(c).replaceAll("[@!+]", "");
  }

}
