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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

/**
 * An entity list for the OME data model.
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

  public String version() { return value("version"); }

  public boolean spwImport() { return !"false".equals(value("spwImport")); }

  // -- MetaEntityList API methods - entities --

  public String schema() {
    String schema = value("schema");
    if (schema == null) {
      schema = "ome_xsd.html";
    }
    return schema;
  }

  public String anchor() {
    String anchor = value("anchor");
    if (anchor == null) {
      anchor = ent + "_" + prop;
    }
    return anchor;
  }

  /** Whether the entity can appear multiple times. */
  public boolean countable() { return "true".equals(value("countable")); }

  public String path() { return value("path"); }

  public String defaultPath() { return value("path", null, ent, prop); }

  /** List of nodes in the path, with markup symbols. Derived from path. */
  public String[] pathNodes() { return path().split("\\/"); }

  /** Last node in the path, without markup symbols. Derived from path. */
  public String last() { return last(path()); }

  /** Path without its last <em>countable</em> node. Derived from path. */
  public String chop() { return chop(path()); }

  /** List of indices in the path. Derived from path. */
  public List<String> indices() { return indices(path()); }

  /** List of method arguments for the path indices. Derived from path. */
  public String argsList() { return argsList(defaultPath()); }

  /** List of types for the path indices. Derived from path. */
  public String typesList() { return typesList(defaultPath()); }

  /** List of variables for the path indices. Derived from path. */
  public String varsList() { return varsList(path(), defaultPath()); }

  /**
   * List of distinct path values for the active version, including sub-paths.
   * Derived from path values of all entities and properties.
   */
  public List<String> unique() {
    HashSet<String> set = new HashSet<String>();
    List<String> unique = new ArrayList<String>();
    for (String entity : entities.keySet()) {
      Entity e = entities.get(entity);
      for (String property : e.props.keySet()) {
        String path = value("path", ver, entity, property);
        if (path.equals("-")) continue;
        while (true) {
          if (set.contains(path)) break; // already processed this path
          set.add(path);
          unique.add(path);
          int slash = path.lastIndexOf("/");
          if (slash < 0) break;
          path = path.substring(0, slash);
        }
      }
    }
    Collections.sort(unique);
    return unique;
  }

  // -- MetaEntityList API methods - properties --

  public String type() { return value("type"); }

  public String defaultType() { return value("type", null, ent, prop); }

  public String getter(String nodeVar) {
    String getter = value("getter");
    String s = getter == null ? "get" + name() : getter;
    s = nodeVar + "." + s;

    // inject conversion method if needed
    String type = type();
    String defaultType = defaultType();
    if (type.equals(defaultType)) s = s + "()";
    else s = var(type) + "To" + defaultType + "(" + s + "())";
    return s;
  }

  public String setter(String nodeVar, String value) {
    String setter = value("setter");
    String s = setter == null ? "set" + name() : setter;

    // inject conversion method if needed
    String type = type();
    String defaultType = defaultType();
    if (type.equals(defaultType)) s = s + "(" + value + ")";
    else s = s + "(" + var(defaultType) + "To" + type + "(" + value + "))";
    return nodeVar + "." + s;
  }

  public String enumeration() {
    return value("enumeration");
  }

  public String defaultValue() {
    return value("defaultValue");
  }

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

  // NB: These methods could be static, but are instance methods
  // to make it easier for Velocity templates to reference them.

  /** Converts name in CamelCase to variable in variableCase. */
  public String var(String s) {
    char[] c = s.toCharArray();
    for (int i=0; i<c.length; i++) {
      if (c[i] >= 'A' && c[i] <= 'Z') c[i] += 'a' - 'A';
      else {
        if (i > 1) c[i - 1] += 'A' - 'a'; // keep last character capitalized
        break;
      }
    }
    return new String(c).replaceAll("[^\\w]", "");
  }

  /** Converts the given type from Java to Slice syntax. */
  public String ice(String s) {
    s = s.toLowerCase();
    s = s.replaceAll("\\[\\]", "Seq");
    s = s.replaceAll("integer", "int");
    s = s.replaceAll("boolean", "bool");
    if (s.endsWith("Seq")) {
      // capitalize first letter
      char[] c = s.toCharArray();
      if (c[0] >= 'a' && c[0] <= 'z') c[0] += 'A' - 'a';
      s = new String(c);
    }
    return s;
  }

  /** Converts the given wrapper class name to the equivalent primitive. */
  public String primitive(String s) {
    if (s.equals("Boolean")) return "boolean";
    if (s.equals("Character")) return "char";
    if (s.equals("Byte")) return "byte";
    if (s.equals("Double")) return "double";
    if (s.equals("Float")) return "float";
    if (s.equals("Integer")) return "int";
    if (s.equals("Long")) return "long";
    if (s.equals("Short")) return "short";
    return s;
  }

  /** Gets the last node in the given path, without markup symbols. */
  public String last(String path) {
    int first = path.lastIndexOf("/") + 1;
    return path.substring(first).replaceAll("\\+", "");
  }

  /** Gets a path without its last <em>countable</em> node. */
  public String chop(String path) {
    int plus = path.lastIndexOf("+");
    if (plus < 0) return null;
    int slash = path.lastIndexOf("/", plus);
    return slash < 0 ? "" : path.substring(0, slash);
  }

  /** List of indices in the given path. */
  public List<String> indices(String path) {
    List<String> list = new ArrayList<String>();
    StringTokenizer st = new StringTokenizer(path, "/");
    int tokens = st.countTokens();
    for (int i=0; i<tokens; i++) {
      String t = st.nextToken();
      if (t.endsWith("+")) list.add(t.replaceAll("\\+", ""));
    }
    return list;
  }

  /**
   * Gets a list of method arguments for the indices in the given path.
   * E.g., "int imageIndex, int pixelsIndex, int planeIndex".
   */
  public String argsList(String path) {
    return indicesList(path, path, true, true);
  }

  /**
   * Gets a list of types for the indices in the given path.
   * E.g., "int, int, int".
   */
  public String typesList(String path) {
    return indicesList(path, path, true, false);
  }

  /**
   * Gets a list of variables for the indices in the given path.
   * E.g., "imageIndex, pixelsIndex, planeIndex".
   */
  public String varsList(String path) {
    return varsList(path, path);
  }

  /**
   * Gets a list of variables for the indices in the given path.
   * E.g., "imageIndex, pixelsIndex, planeIndex".
   */
  public String varsList(String path, String defaultPath) {
    return indicesList(path, defaultPath, false, true);
  }

  private String indicesList(String path, String defaultPath,
    boolean doTypes, boolean doVars)
  {
    StringBuffer sb = new StringBuffer();
    List<String> indices = indices(path);
    List<String> defaultIndices = indices(defaultPath);
    while (defaultIndices.size() > indices.size()) {
      defaultIndices.remove(defaultIndices.size() - 1);
    }
    boolean first = true;
    for (String index : defaultIndices) {
      if (first) first = false;
      else sb.append(", ");
      if (doTypes) sb.append("int");
      if (doTypes && doVars) sb.append(" ");
      if (doVars) sb.append(var(index + "Index"));
    }
    return sb.toString();
  }

}
