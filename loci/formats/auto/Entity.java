//
// Entity.java
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

import java.util.*;

/**
 * CTR TODO Entity javadoc.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/auto/Entity.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/auto/Entity.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class Entity {

  // -- Fields --

  /** Name of the entity. */
  private String name;

  /** Sentence fragment description of the entity. */
  private String desc;

  /** Extra sentences to tack on to the Javadoc description. */
  private String extra;

  /**
   * List of node paths for the entity.
   * Keys are version strings, values are path strings.
   */
  private Hashtable paths = new Hashtable();

  /** List of properties associated with the entity. */
  private Vector props = new Vector();

  /** List of properties corresponding to needed indices for a node. */
  private Vector indices = new Vector();

  // -- Constructor --

  public Entity(String name, String desc, String extra) {
    this.name = name;
    this.desc = desc;
    this.extra = extra;
  }

  // -- Entity API methods --

  public String name() { return name; }

  /** Javadoc for this entity's MetadataStore setter method. */
  public String doc() {
    StringBuffer sb = new StringBuffer();
    sb.append("Sets ");
    sb.append(desc);
    sb.append(".");
    if (!"-".equals(extra)) {
      sb.append(" ");
      sb.append(extra);
    }
    return sb.toString();
  }

  public Hashtable paths() { return paths; }

  public String path(String version) {
    String s = (String) paths.get(version);
    return s == null ? (String) paths.get("Default") : s;
  }

  public String[] pathNodes(String version) {
    return path(version).split("\\/");
  }

  public String last(String version) {
    String path = path(version);
    int first = path.lastIndexOf("/") + 1;
    return path.substring(first).replaceAll("[@\\!\\+]", "");
  }

  public String lastVar(String version) {
    return toVarName(last(version));
  }

  public Vector props() { return props; }

  public Vector indices() { return indices; }

  public String argsList(boolean doTypes, boolean doVars) {
    StringBuffer sb = new StringBuffer();
    int psize = props.size();
    int isize = indices.size();
    int total = psize + isize;
    for (int i=0; i<total; i++) {
      Property p;
      if (i < psize) p = (Property) props.get(i);
      else p = (Property) indices.get(i - psize);
      if (i > 0) sb.append(", ");
      if (doTypes) sb.append(p.type());
      if (doTypes && doVars) sb.append(" ");
      if (doVars) sb.append(p.varName());
    }
    return sb.toString();
  }

  public String indicesList(boolean doTypes, boolean doVars) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<indices.size(); i++) {
      Property p = (Property) indices.get(i);
      if (i > 0) sb.append(", ");
      if (doTypes) sb.append(p.type());
      if (doTypes && doVars) sb.append(" ");
      if (doVars) sb.append(p.varName());
    }
    return sb.toString();
  }

  /** Adds a path override for a particular metadata version. */
  public void addPath(String version, String path) {
    paths.put(version, path);
    if (version.equals("Default")) {
      // compute indices
      StringTokenizer st = new StringTokenizer(path, "/");
      int tokens = st.countTokens();
      for (int i=0; i<tokens; i++) {
        String t = st.nextToken();
        if (t.endsWith("+")) {
          t = t.substring(t.startsWith("@") ? 1 : 0, t.length() - 1);
          Property p = new Property(t + "Index",
            "Integer", "index of the " + t, this, true);
          indices.add(p);
        }
      }
    }
  }

  /** Converts name in CamelCase to variable in variableCase. */
  public String toVarName(String s) {
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
    return new String(c);
  }

  // -- Internal Entity API methods --

  /** Adds a property associated with this entity. */
  protected void addProperty(Property prop) {
    props.add(prop);
  }

}
