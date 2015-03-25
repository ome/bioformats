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
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;

/**
 * An EntityList is a list of entities parsed from an INI-style resource,
 * represented as a hierarchical tree structure. Here is a partial example:
 * <pre>
 * + entity list
 * |--+ versions
 * |  |--+ 2003FC
 * |  |   ...
 * |  |--+ 200706
 * |  |   \- name = 200706
 * |  |   \- class = OMEXML200706Metadata
 * |  |   \- id = OMEXML200706Metadata.java
 * |  |   \- basePackage = ome.xml.r200706
 * |  |   \- subPackage = ome
 * |  |   \- version = 2007-06
 * |  |--+ 200802
 * |  |   ...
 * |  |...
 * |--+ entities
 *    |--+ Image
 *    |  ...
 *    |--+ Pixels
 *    |  ...
 *    |--+ Dimensions
 *    |  |--+ properties
 *    |  |   \- path = Image+/Pixels+
 *    |  |   \- description = A set of pixel dimensions in the metadata store.
 *    |  |--+ entity version overrides
 *    |  |  |--+ 2003FC
 *    |  |      \- path = Image+/CA/Dimensions
 *    |  |--+ properties
 *    |     |--+ PhysicalSizeX
 *    |     |  |--+ properties
 *    |     |  |   \- type = Float
 *    |     |  |   \- description = Width of each pixel in microns.
 *    |     |  |--+ property version overrides
 *    |     |     |-+ 2003FC
 *    |     |        \- getter = getPixelSizeX
 *    |     |        \- setter = setPixelSizeX
 *    |     |--+ PhysicalSizeY
 *    |     |  |--+ properties
 *    |     |  |   \- type = Float
 *    |     |  |   \- description = Height of each pixel in microns.
 *    |     |  |--+ property version overrides
 *    |     |     |-+ 2003FC
 *    |     |        \- getter = getPixelSizeY
 *    |     |        \- setter = setPixelSizeY
 *    |     |--+ PhysicalSizeZ
 *    |     |  |--+ properties
 *    |     |  |   \- type = Float
 *    |     |  |   \- description = Distance between focal planes in microns.
 *    |     |  |--+ property version overrides
 *    |     |     |-+ 2003FC
 *    |     |        \- getter = getPixelSizeZ
 *    |     |        \- setter = setPixelSizeZ
 *    |     |--+ TimeIncrement
 *    |     |  |--+ properties
 *    |     |  |   \- type = Float
 *    |     |  |   \- description = Distance between time points in seconds.
 *    |     |  |--+ property version overrides
 *    |     |     |-+ 2003FC
 *    |     |        \- getter = getPixelSizeT
 *    |     |        \- setter = setPixelSizeT
 *    |     |--+ WaveStart
 *    |     |  |--+ properties
 *    |     |  |   \- type = Integer
 *    |     |  |   \- description = Starting wavelength in nanometers.
 *    |     |  |--+ property version overrides
 *    |     |     |-+ 2003FC
 *    |     |        \- available = false
 *    |     |--+ WaveIncrement
 *    |        |--+ properties
 *    |        |   \- type = Integer
 *    |        |   \- description = Distance between wavelengths in nanometers.
 *    |        |--+ property version overrides
 *    |           |-+ 2003FC
 *    |              \- type = Float
 *    |              \- getter = getPixelSizeC
 *    |              \- setter = setPixelSizeC
 *    |...
 * </pre>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class EntityList {

  // -- Constants --

  private static final boolean DEBUG = false;

  // -- Fields --

  /** List of versions and their details. */
  protected HashMap<String, HashMap<String, String>> versions =
    new HashMap<String, HashMap<String, String>>();

  /** List of entities. */
  protected HashMap<String, Entity> entities =
    new HashMap<String, Entity>();

  /** The active version. */
  protected String ver;

  /** The active entity. */
  protected String ent;

  /** The active property. */
  protected String prop;

  // -- Constructor --

  /** Constructs an entity list. */
  public EntityList(String versionPath, String entityPath) throws IOException {
    // parse INI data
    System.out.println("Parsing configuration data");
    IniParser parser = new IniParser();
    IniList versionList = parser.parseINI(versionPath, EntityList.class);
    IniList entityList = parser.parseINI(entityPath, EntityList.class);

    // convert unprocessed INI-style config data into
    // hierarchical entity/property/version structure

    // process list of versions
    for (HashMap<String, String> attrs : versionList) {
      String name = attrs.get(IniTable.HEADER_KEY);
      versions.put(name, attrs);
    }

    // process list of entities
    entities = new HashMap<String, Entity>();
    for (HashMap<String, String> attrs : entityList) {
      String name = attrs.get(IniTable.HEADER_KEY);
      if (DEBUG) debug("-- Parsing entry '" + name + "' --");

      // "entity" list actually contains attribute tables for entities,
      // properties and version overrides; determine which kind this one is
      StringTokenizer st = new StringTokenizer(name);
      int num = st.countTokens();
      switch (num) {
        case 1:
          // entity header
          if (DEBUG) debug("Entry is an entity definition");
          entities.put(name, new Entity(attrs));
          System.out.println("\t" + name);
          if (DEBUG) debug("Added entity '" + name + "'");
          break;
        case 2:
          // property header, or entity version header
          String t1 = st.nextToken();
          String t2 = st.nextToken();
          Entity entity = entities.get(t1);
          if (entity == null) {
            throw new IOException("bad entity name '" + t1 + "'");
          }
          attrs.put(IniTable.HEADER_KEY, t2);
          if (DEBUG) debug("Reset header value to '" + t2 + "'");
          HashMap<String, String> version = versions.get(t2);
          if (version == null) {
            // property header
            if (DEBUG) debug("Entry is a property definition");
            entity.props.put(t2, new Property(attrs));
            if (DEBUG) {
              debug("Added property '" + t2 + "' to entity '" + t1 + "'");
            }
          }
          else {
            // entity version header
            if (DEBUG) debug("Entry is an entity version definition");
            entity.versions.put(t2, attrs);
            if (DEBUG) {
              debug("Added version '" + t2 + "' to entity '" + t1 + "'");
            }
          }
          break;
        case 3:
          // property version header
          if (DEBUG) debug("Entry is a property version definition");
          t1 = st.nextToken();
          t2 = st.nextToken();
          String t3 = st.nextToken();
          entity = entities.get(t1);
          if (entity == null) {
            throw new IOException("bad entity name '" + t1 + "'");
          }
          Property prop = entity.props.get(t2);
          if (prop == null) {
            throw new IOException("bad property name '" +
              t2 + "' for entity '" + t1 + "'");
          }
          attrs.put(IniTable.HEADER_KEY, t2);
          if (DEBUG) debug("Reset header value to '" + t2 + "'");
          prop.versions.put(t3, attrs);
          if (DEBUG) {
            debug("Added property '" + t2 +
              "' to version '" + t3 + "' of entity '" + t1 + "'");
          }
          break;
        default:
          throw new IOException("bad token count for header '" + name + "'");
      }
    }
  }

  // -- EntityList API methods --

  /** Sets the active version. */
  public void setVersion(String version) {
    ver = version;
  }

  /** Gets the active version. */
  public String getVersion() {
    return ver;
  }

  /** Sets the active entity. */
  public void setEntity(String entity) {
    ent = entity;
    prop = null;
  }

  /** Gets the active entity. */
  public String getEntity() {
    return ent;
  }

  /** Sets the active property. */
  public void setProperty(String property) {
    prop = property;
  }

  /** Gets the active property. */
  public String getProperty() {
    return prop;
  }

  /** Gets list of versions in sorted order. */
  public List<String> versions() {
    List<String> list = new ArrayList<String>(versions.keySet());
    Collections.sort(list);
    return list;
  }

  /** Gets the list of entities in sorted order. */
  public List<String> entities() {
    List<String> list = new ArrayList<String>(entities.keySet());
    Collections.sort(list);
    return list;
  }

  /** Gets a sorted list of properties for the active entity. */
  public List<String> props() {
    if (ent == null) return null;
    Entity e = entities.get(ent);
    List<String> list = new ArrayList<String>(e.props.keySet());
    Collections.sort(list);
    return list;
  }

  /** Gets the attribute value for the header key. */
  public String name() { return value(IniTable.HEADER_KEY); }

  /**
   * Queries the attribute value for the given key, for the active version,
   * property and entity.
   */
  public String value(String key) {
    return value(key, ver, ent, prop);
  }

  /**
   * Queries the attribute value for the given key, for a particular version of
   * a particular property of a particular entity. The attribute value is
   * obtained from an attributes table according to following order of
   * precedence:
   * <ol>
   *   <li>version override of the given property of the given entity</li>
   *   <li>default value of the given property of the given entity</li>
   *   <li>version override of the given entity</li>
   *   <li>default value of the given entity itself</li>
   *   <li>default value in the given version's attribute table</li>
   * </ol>
   * Returns null if the attribute is not found in any of the above tables.
   */
  public String value(String key,
    String version, String entity, String property)
  {
    Entity e = null;
    Property p = null;
    if (entity != null) e = entities.get(entity);
    if (property != null && e != null) p = e.props.get(property);

    String value = null;

    // version override of the given property of the given entity
    if (value == null && version != null && p != null) {
      HashMap<String, String> attrs = p.versions.get(version);
      if (attrs != null) value = attrs.get(key);
    }

    // default value of the given property of the given entity
    if (value == null && p != null) value = p.attrs.get(key);

    // version override of the given entity
    if (value == null && version != null && e != null) {
      HashMap<String, String> attrs = e.versions.get(version);
      if (attrs != null) value = attrs.get(key);
    }

    // default value of the given entity itself
    if (value == null && e != null) value = e.attrs.get(key);

    // default value in the given version's attribute table
    if (value == null && version != null) {
      HashMap<String, String> attrs = versions.get(version);
      if (attrs != null) value = attrs.get(key);
    }

    return value;
  }

  // -- Helper methods --

  private void debug(String s) {
    System.out.println("DEBUG: " + s);
  }

}
