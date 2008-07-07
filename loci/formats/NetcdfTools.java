//
// NetcdfTools.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

import java.io.*;
import java.util.*;

/**
 * Utility class for working with NetCDF/HDF files.  Uses reflection to
 * call the NetCDF Java library.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/NetcdfTools.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/NetcdfTools.java">SVN</a></dd></dl>
 */
public class NetcdfTools {

  // -- Constants --

  private static final String NO_NETCDF_MSG =
    "NetCDF is required to read NetCDF/HDF variants.  Please obtain " +
    "the necessary JAR files from http://loci.wisc.edu/ome/formats.html";

  // -- Fields --

  private boolean noNetCDF;
  private ReflectedUniverse r;
  private String currentFile;
  private Vector attributeList;
  private Vector variableList;

  // -- Constructor --

  public NetcdfTools(String file) {
    this.currentFile = file;
    initialize();
  }

  // -- NetcdfTools API methods --

  public Vector getAttributeList() {
    return attributeList;
  }

  public Vector getVariableList() {
    return variableList;
  }

  public String getAttributeValue(String name) {
    String dir = name.substring(0, name.lastIndexOf("/"));
    String attr = name.substring(name.lastIndexOf("/") + 1);

    try {
      setupGroup(dir);

      r.setVar("name", attr);
      Object v = r.exec("attribute = g.findAttribute(name)");
      if (v == null) return null;

      Boolean isString = (Boolean) r.exec("isString = attribute.isString()");
      if (isString.booleanValue()) {
        r.exec("array = attribute.getValues()");
        Object[] s = (Object[]) r.exec("s = array.copyTo1DJavaArray()");
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<s.length; i++) {
          sb.append((String) s[i]);
        }
        return sb.toString();
      }
    }
    catch (ReflectException e) {
      LogTools.trace(e);
    }
    return null;
  }

  public Object getVariableValue(String name) {
    String dir = name.substring(0, name.lastIndexOf("/"));
    String attr = name.substring(name.lastIndexOf("/") + 1);

    try {
      setupGroup(dir);

      r.setVar("name", attr);
      r.exec("var = g.findVariable(name)");
      r.exec("data = var.read()");
      return r.exec("data = data.copyToNDJavaArray()");
    }
    catch (ReflectException e) {
      LogTools.trace(e);
    }
    return null;
  }

  public Hashtable getVariableAttributes(String name) {
    String dir = name.substring(0, name.lastIndexOf("/"));
    String attr = name.substring(name.lastIndexOf("/") + 1);

    try {
      setupGroup(dir);

      r.setVar("name", attr);
      r.exec("var = g.findVariable(name)");
      List l = (List) r.exec("attributes = var.getAttributes()");

      Hashtable h = new Hashtable();
      for (int i=0; i<l.size(); i++) {
        r.setVar("attr", l.get(i));

        r.exec("array = attr.getValues()");
        h.put(r.exec("attr.getName()"),
          r.exec("s = array.copyTo1DJavaArray()"));
      }

      return h;
    }
    catch (ReflectException e) {
      LogTools.trace(e);
    }
    return null;
  }

  public int getDimension(String name) {
    String dir = name.substring(0, name.lastIndexOf("/"));
    String attr = name.substring(name.lastIndexOf("/") + 1);

    try {
      setupGroup(dir); r.setVar("name", attr); r.exec("dim = g.findDimension(name)");
      return ((Integer) r.exec("dim.getLength()")).intValue();
    }
    catch (ReflectException e) {
      LogTools.trace(e);
    }
    return -1;
  }

  public void close() {
    try {
      r.exec("ncfile.close()");
    }
    catch (ReflectException e) { }
  }

  // -- Helper methods --

  private void initialize() {
    r = null;
    try {
      r = new ReflectedUniverse();
      r.exec("import ucar.ma2.Array");
      r.exec("import ucar.ma2.ArrayByte");
      r.exec("import ucar.nc2.Attribute");
      r.exec("import ucar.nc2.Group");
      r.exec("import ucar.nc2.NetcdfFile");
    }
    catch (ReflectException exc) {
      noNetCDF = true;
      LogTools.trace(exc);
    }
    catch (UnsupportedClassVersionError exc) {
      noNetCDF = true;
      LogTools.trace(exc);
    }

    // HACK - NetCDF prints a fair number of warning messages to stdout
    // we need to filter these out so that they don't interfere with omebf
    PrintStream out = new PrintStream(System.out) {
      public void print(String s) {
        if (s == null || !s.trim().startsWith("WARN:")) super.print(s);
      }
      public void println(String s) {
        if (s == null || !s.trim().startsWith("WARN:")) super.println(s);
      }
    };
    System.setOut(out);

    try {
      r.setVar("currentId", Location.getMappedId(currentFile));
      r.exec("ncfile = NetcdfFile.open(currentId)");
      r.exec("root = ncfile.getRootGroup()");

      attributeList = new Vector();
      variableList = new Vector();
      Vector groups = new Vector();
      groups.add(r.getVar("root"));
      parseAttributesAndVariables(groups);
    }
    catch (ReflectException e) {
      LogTools.trace(e);
    }
  }

  private void parseAttributesAndVariables(List groups) {
    for (int i=0; i<groups.size(); i++) {
      try {
        r.setVar("group", groups.get(i));
        String groupName = (String) r.exec("groupName = group.getName()");

        List l = (List) r.exec("attributes = group.getAttributes()");
        for (int j=0; j<l.size(); j++) {
          r.setVar("attr", l.get(j));
          String name = (String) r.exec("name = attr.getName()");
          if (!groupName.endsWith("/")) name = "/" + name;
          attributeList.add(groupName + name);
        }

        l = (List) r.exec("variables = group.getVariables()");
        for (int j=0; j<l.size(); j++) {
          r.setVar("var", l.get(j));
          String name = (String) r.exec("name = var.getName()");
          if (!groupName.endsWith("/")) name = "/" + name;
          variableList.add(groupName + name);
        }

        List g = (List) r.exec("groups = group.getGroups()");
        parseAttributesAndVariables(g);
      }
      catch (ReflectException e) {
        LogTools.trace(e);
      }
    }
  }

  private Object findGroup(String name, String parent, String store) {
    try {
      r.setVar("name", name);
      return r.exec(store + " = " + parent + ".findGroup(name)");
    }
    catch (ReflectException e) {
      LogTools.trace(e);
    }
    return null;
  }

  private void setupGroup(String name) throws ReflectException {
    if (name.indexOf("/") == -1) {
      r.setVar("g", r.getVar("root"));
      return;
    }
    String dir = name.substring(0, name.lastIndexOf("/"));
    String attr = name.substring(name.lastIndexOf("/") + 1);

    StringTokenizer tokens = new StringTokenizer(dir, "/");
    String parent = null;
    while (tokens.hasMoreTokens()) {
      String token = tokens.nextToken();
      findGroup(token, parent == null ? "root" : "g", "g");
      if (tokens.hasMoreTokens()) parent = token;
    }
    if (parent == null) r.setVar("g", r.getVar("root"));
  }

}
