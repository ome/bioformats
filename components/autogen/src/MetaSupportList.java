/*
 * #%L
 * Bio-Formats autogen package for programmatically generating source code.
 * %%
 * Copyright (C) 2007 - 2017 Open Microscopy Environment:
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
import loci.common.ReflectException;
import loci.common.ReflectedUniverse;
import loci.formats.IFormatHandler;

/**
 * An ugly data structure for organizing the status of each metadata property
 * for each format handler (readers and writers).
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class MetaSupportList {

  // -- Constants --

  /** Path to metadata groups definition file. */
  public static final String GROUPS_SRC = "meta-groups.txt";

  /** Path to supported metadata properties definition file. */
  public static final String SUPPORT_SRC = "meta-support.txt";

  public static final String YES = "Yes";
  public static final String NO = "No";
  public static final String PARTIAL = "Partial";
  public static final String MISSING = "Missing";

  // -- Fields --

  /** List of all metadata properties. */
  protected MetaEntityList entityList;

  /** List of groups. Key is group name, value is list of properties. */
  protected HashMap<String, List<String>> groups =
    new HashMap<String, List<String>>();

  /**
   * List of supported properties. Key is handler name (e.g., AVIReader),
   * value is a table mapping from properties to support tags
   * (YES, NO, PARTIAL or MISSING).
   */
  protected HashMap<String, HashMap<String, String>> supported =
    new HashMap<String, HashMap<String, String>>();

  protected HashMap<String, String> pagenames = new HashMap<String, String>();

  /** Version of OME-XML (e.g., 2008-02). */
  protected String version;

  /** Current handler (e.g., AVIReader). */
  protected String handlerName;

  // -- Constructors --

  /** Constructs an entity list. */
  public MetaSupportList(String version) throws IOException {
    this(version, GROUPS_SRC, SUPPORT_SRC);
  }

  /** Constructs an entity list. */
  public MetaSupportList(String version, String groupsPath, String supportPath)
    throws IOException
  {
    this.version = version;

    // parse metadata properties
    entityList = new MetaEntityList();
    entityList.setVersion(version);

    // parse INI data
    IniParser parser = new IniParser();
    IniList groupsList = parser.parseINI(groupsPath, MetaSupportList.class);
    IniList supportList = parser.parseINI(supportPath, MetaSupportList.class);

    // convert unprocessed INI-style config data into data structures

    // process list of groups
    HashMap<String, String> groupHash = groupsList.get(0);
    for (String groupName : groupHash.keySet()) {
      String propString = groupHash.get(groupName);
      StringTokenizer st = new StringTokenizer(propString, " ");
      List<String> propList = new ArrayList<String>();
      while (st.hasMoreTokens()) {
        String prop = st.nextToken();
        propList.add(prop);
      }
      Collections.sort(propList);
      groups.put(groupName, propList);
    }

    // process list of supported metadata properties
    for (HashMap<String, String> propHash : supportList) {
      String handler = propHash.get(IniTable.HEADER_KEY);
      propHash.remove(IniTable.HEADER_KEY);
      supported.put(handler, propHash);
    }
  }

  // -- MetaSupportList API methods --

  /** Gets the list of entities associated with the data structure. */
  public MetaEntityList entityList() { return entityList; }

  /** Gets the version of OME-XML to which entities should be linked. */
  public String version() { return version; }

  /** Gets a list of all known handlers. */
  public List<String> handlers() {
    List<String> handlers = new ArrayList<String>();
    for (String handler : supported.keySet()) handlers.add(handler);
    Collections.sort(handlers);
    return handlers;
  }

  /** Sets the current handler (e.g., AVIReader). */
  public void setHandler(String name) { handlerName = name; }

  /** Gets the current handler. */
  public String handler() { return handlerName; }

  /** Gets the name of the format for the current handler. */
  public String format() {
    ReflectedUniverse r = new ReflectedUniverse();
    IFormatHandler handler;
    try {
      r.exec("import loci.formats.in." + handlerName);
    }
    catch (ReflectException exc) { }
    try {
      r.exec("import loci.formats.out." + handlerName);
    }
    catch (ReflectException exc) { }
    try {
      handler = (IFormatHandler) r.exec("new " + handlerName + "()");
      return handler.getFormat();
    }
    catch (ReflectException exc) { }
    return null;
  }

  /** Gets the type (reader or writer) for the current handler. */
  public String handlerType() {
    ReflectedUniverse r = new ReflectedUniverse();
    try {
      r.exec("import loci.formats.in." + handlerName);
      return "reader";
    }
    catch (ReflectException exc) { }
    try {
      r.exec("import loci.formats.out." + handlerName);
      return "writer";
    }
    catch (ReflectException exc) { }
    return "handler";
  }

  /** Gets the documentation page name corresponding to this handler. */
  public String getPageName() {
    return pagenames.get(handlerName);
  }

  /** Sets the documentation page name corresponding to this handler. */
  public void setPageName(String pagename) {
    pagenames.put(handlerName, pagename);
  }

  /** Gets a list of all known groups. */
  public List<String> groups() {
    List<String> groupList = new ArrayList<String>();
    for (String group : groups.keySet()) groupList.add(group);
    Collections.sort(groupList);
    return groupList;
  }

  /** Gets the list of properties belonging to the given group. */
  public List<String> groupMembers(String group) {
    return groups.get(group);
  }

  /** Gets all supported properties for the current handler. */
  public List<String> yes() { return getSupportValue(YES); }

  /** Gets all unsupported properties for the current handler. */
  public List<String> no() { return getSupportValue(NO); }

  /** Gets all partially supported properties for the current handler. */
  public List<String> partial() { return getSupportValue(PARTIAL); }

  /** Gets all inapplicable properties for the current handler. */
  public List<String> missing() { return getSupportValue(MISSING); }

  /** Gets the number of handlers that support the given property. */
  public int yesHandlerCount(String entity, String prop) {
    return getHandlerCount(entity, prop, YES);
  }

  /** Gets the number of handlers that do not support the given property. */
  public int noHandlerCount(String entity, String prop) {
    return getHandlerCount(entity, prop, NO);
  }

  /** Gets the number of handlers that partially support the given property. */
  public int partialHandlerCount(String entity, String prop) {
    return getHandlerCount(entity, prop, PARTIAL);
  }

  /**
   * Gets the number of handlers for which the given property is inapplicable.
   */
  public int missingHandlerCount(String entity, String prop) {
    return getHandlerCount(entity, prop, MISSING);
  }

  /** Extracts entity from a string of the form "Entity.Property comment". */
  public String entity(String s) {
    int dot = s.indexOf(".");
    return s.substring(0, dot);
  }

  /** Extracts property from a string of the form "Entity.Property comment". */
  public String prop(String s) {
    int dot = s.indexOf(".");
    int space = s.indexOf(" ");
    return space < 0 ? s.substring(dot + 1) : s.substring(dot + 1, space);
  }

  /** Extracts comment from a string of the form "Entity.Property comment". */
  public String comment(String s) {
    int space = s.indexOf(" ");
    if (space < 0) return "";
    return s.substring(space + 1).trim();
  }

  /** Looks up the node type corresponding to the given entity. */
  public String node(String entity) {
    entityList.setEntity(entity);
    return entityList.last();
  }

  // -- Helper methods --

  /** Gets properties with the given support value for the current handler. */
  protected List<String> getSupportValue(String supportValue) {
    List<String> props = new ArrayList<String>();

    // for this handler, get table mapping properties to support tags
    HashMap<String, String> supportProps = supported.get(handlerName);

    // flag whether we are looking for missing entries
    boolean missing = MISSING.equals(supportValue);

    // check every property against the table mapping
    for (String entity : entityList.entities()) {
      entityList.setEntity(entity);

      // check if this entity is set to this support value;
      // if so, this is a shortcut for all properties of that entity
      String entitySupportValue = supportProps.get(entity);
      String entitySince = null;
      int space = entitySupportValue == null ?
        -1 : entitySupportValue.indexOf(" ");
      if (space >= 0) {
        entitySince = entitySupportValue.substring(space).trim();
        entitySupportValue = entitySupportValue.substring(0, space).trim();
      }
      boolean all = supportValue.equals(entitySupportValue);

      // if we are looking for missing entries, mark if entity is missing
      boolean entityMissing = missing && entitySupportValue == null;

      for (String prop : entityList.props()) {
        // properties are listed with the convention "Entity.Property"
        String fqProp = entity + "." + prop;
        String propSupportValue = supportProps.get(fqProp);
        String comment = null;
        if (propSupportValue == null || // is there no property support value?
          // do entity and property support values match?
          (entitySupportValue != null &&
          entitySupportValue.equals(propSupportValue)))
        {
          // set the property's default comment text to match the entity
          comment = entitySince;
        }
        space = propSupportValue == null ? -1 : propSupportValue.indexOf(" ");
        if (space >= 0) {
          comment = propSupportValue.substring(space).trim();
          propSupportValue = propSupportValue.substring(0, space).trim();
        }
        // property matches support value if it either matches directly,
        // or its parent entity matches globally and no override exists
        if (supportValue.equals(propSupportValue) || // direct match?
          // does parent entity match globally with no override?
          (all && propSupportValue == null) ||
          // are we are looking for missing entries with a missing property?
          (entityMissing && propSupportValue == null))
        {
          props.add(comment == null ? fqProp : fqProp + " " + comment);
        }
      }
    }
    Collections.sort(props);
    return props;
  }

  /**
   * Gets the number of handlers that match the
   * given support value for the specified property.
   */
  protected int getHandlerCount(String entity,
    String prop, String supportValue)
  {
    // properties are listed with the convention "Entity.Property"
    String fqProp = entity + "." + prop;
    boolean missing = MISSING.equals(supportValue);
    int handlerCount = 0;
    for (String handler : supported.keySet()) {
      HashMap<String, String> supportProps = supported.get(handler);
      String propSupportValue = supportProps.get(fqProp);
      if (propSupportValue != null) {
        int space = propSupportValue.indexOf(" ");
        if (space >= 0) propSupportValue = propSupportValue.substring(0, space);
        if (propSupportValue.equals(supportValue)) {
          // specific property matches desired support value
          handlerCount++;
          continue;
        }
      }
      // check if the entity is set to this support value;
      // if so, this is a shortcut for all properties of that entity
      String entitySupportValue = supportProps.get(entity);
      if (entitySupportValue != null) {
        int space = entitySupportValue.indexOf(" ");
        if (space >= 0) {
          entitySupportValue = entitySupportValue.substring(0, space);
        }
        if (entitySupportValue.equals(supportValue)) {
          // parent entity matches desired support value
          handlerCount++;
          continue;
        }
      }
      // check if property is missing and we are looking for missing entries
      if (propSupportValue == null && entitySupportValue == null && missing) {
        handlerCount++;
        continue;
      }
    }
    return handlerCount;
  }

}
