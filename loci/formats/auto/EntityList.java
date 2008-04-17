//
// EntityList.java
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
 * |  |   \- legacy = false
 * |  |--+ 200802
 * |  |   ...
 * |  |...
 * |--+ entities
 *    |--+ Image
 *    |  ...
 *    |--+ Pixels
 *    |  ...
 *    |--+ Dimensions
 *    |  |--+ attributes
 *    |  |   \- path = Image+/Pixels+
 *    |  |   \- description = A set of pixel dimensions in the metadata store.
 *    |  |--+ entity version overrides
 *    |  |  |--+ 2003FC
 *    |  |      \- path = Image+/CA/Dimensions
 *    |  |--+ properties
 *    |     |--+ PhysicalSizeX
 *    |     |  |--+ attributes
 *    |     |  |   \- type = Float
 *    |     |  |   \- description = Width of each pixel in microns.
 *    |     |  |--+ property version overrides
 *    |     |     |-+ 2003FC
 *    |     |        \- getter = getPixelSizeX
 *    |     |        \- setter = setPixelSizeX
 *    |     |--+ PhysicalSizeY
 *    |     |  |--+ attributes
 *    |     |  |   \- type = Float
 *    |     |  |   \- description = Height of each pixel in microns.
 *    |     |  |--+ property version overrides
 *    |     |     |-+ 2003FC
 *    |     |        \- getter = getPixelSizeY
 *    |     |        \- setter = setPixelSizeY
 *    |     |--+ PhysicalSizeZ
 *    |     |  |--+ attributes
 *    |     |  |   \- type = Float
 *    |     |  |   \- description = Distance between focal planes in microns.
 *    |     |  |--+ property version overrides
 *    |     |     |-+ 2003FC
 *    |     |        \- getter = getPixelSizeZ
 *    |     |        \- setter = setPixelSizeZ
 *    |     |--+ TimeIncrement
 *    |     |  |--+ attributes
 *    |     |  |   \- type = Float
 *    |     |  |   \- description = Distance between time points in seconds.
 *    |     |  |--+ property version overrides
 *    |     |     |-+ 2003FC
 *    |     |        \- getter = getPixelSizeT
 *    |     |        \- setter = setPixelSizeT
 *    |     |--+ WaveStart
 *    |     |  |--+ attributes
 *    |     |  |   \- type = Integer
 *    |     |  |   \- description = Starting wavelength in nanometers.
 *    |     |  |--+ property version overrides
 *    |     |     |-+ 2003FC
 *    |     |        \- available = false
 *    |     |--+ WaveIncrement
 *    |        |--+ attributes
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
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/auto/EntityList.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/auto/EntityList.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class EntityList {

  // -- Fields --

  /** List of versions and their details. */
  protected Hashtable<String, Hashtable<String, String>> versions =
    new Hashtable<String, Hashtable<String, String>>();

  /** List of entities. */
  protected Hashtable<String, Entity> entities =
    new Hashtable<String, Entity>(); 

  /** The active version. */
  protected String ver;

  /** The active entity. */
  protected String ent;

  /** The active property. */
  protected String prop;

  // -- Constructors --

  /** Constructs an entity list. */
  public EntityList(String versionPath, String entityPath) throws IOException {
    // parse INI data
    System.out.println("Parsing configuration data");
    IniParser parser = new IniParser();
    Vector<Hashtable<String, String>> versionList =
      parser.parseINI(versionPath);
    Vector<Hashtable<String, String>> entityList =
      parser.parseINI(entityPath);

    // convert unprocessed INI-style config data into
    // hierarchical entity/property/version structure

    // process list of versions
    for (Hashtable<String, String> attrs : versionList) {
      String name = attrs.get(IniParser.HEADER_KEY);
      versions.put(name, attrs);
    }

    // process list of entities
    entities = new Hashtable<String, Entity>();
    for (Hashtable<String, String> attrs : entityList) {
      String name = attrs.get(IniParser.HEADER_KEY);

      // "entity" list actually contains attribute tables for entities,
      // properties and version overrides; determine which kind this one is
      StringTokenizer st = new StringTokenizer(name);
      int num = st.countTokens();
      switch (num) {
        case 1:
          // entity header
          entities.put(name, new Entity(attrs));
          System.out.println("\t" + name);
          break;
        case 2:
          // property header, or entity version header
          String t1 = st.nextToken();
          String t2 = st.nextToken();
          Entity entity = entities.get(t1);
          if (entity == null) {
            throw new IOException("bad entity name '" + t1 + "'");
          }
          attrs.put(IniParser.HEADER_KEY, t2);
          Hashtable<String, String> version = versions.get(t2);
          if (version == null) {
            // property header
            entity.props.put(t2, new Property(attrs));
          }
          else {
            // entity version header
            entity.versions.put(t2, attrs);
          }
          break;
        case 3:
          // property version header
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
          prop.versions.put(t3, attrs);
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

  /** Sets the active entity. */
  public void setEntity(String entity) {
    ent = entity;
  }

  /** Sets the active property. */
  public void setProperty(String property) {
    prop = property;
  }

  /** Gets list of versions in sorted order. */
  public Vector<String> versions() {
    Vector<String> list = new Vector<String>(versions.keySet());
    Collections.sort(list);
    return list;
  }

  /** Gets the list of entities in sorted order. */
  public Vector<String> entities() {
    Vector<String> list = new Vector<String>(entities.keySet());
    Collections.sort(list);
    return list;
  }

  /** Gets a sorted list of properties for the active entity. */
  public Vector<String> props() {
    if (ent == null) return null;
    Entity e = entities.get(ent);
    Vector<String> list = new Vector<String>(e.props.keySet());
    Collections.sort(list);
    return list;
  }

  /** Gets the attribute value for the header key. */
  public String name() { return value(IniParser.HEADER_KEY); }

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
      Hashtable<String, String> attrs = p.versions.get(version);
      if (attrs != null) value = attrs.get(key);
    }

    // default value of the given property of the given entity
    if (value == null && p != null) value = p.attrs.get(key);

    // version override of the given entity
    if (value == null && version != null && e != null) {
      Hashtable<String, String> attrs = e.versions.get(version);
      if (attrs != null) value = attrs.get(key);
    }

    // default value of the given entity itself
    if (value == null && e != null) value = e.attrs.get(key);

    // default value in the given version's attribute table
    if (value == null && version != null) {
      Hashtable<String, String> attrs = versions.get(version);
      if (attrs != null) value = attrs.get(key);
    }

    return value;
  }

}
