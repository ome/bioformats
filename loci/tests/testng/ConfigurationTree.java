//
// ConfigurationTree.java
//

/*
LOCI software automated test suite for TestNG. Copyright (C) 2007-@year@
Melissa Linkert and Curtis Rueden. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the UW-Madison LOCI nor the names of its
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE UW-MADISON LOCI ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package loci.tests.testng;

import java.io.*;
import java.util.*;
import javax.swing.tree.DefaultMutableTreeNode;
import loci.formats.FormatTools;
import loci.formats.LogTools;

/**
 * Stores configuration data about files in a directory structure.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/tests/testng/ConfigurationTree.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/tests/testng/ConfigurationTree.java">SVN</a></dd></dl>
 */
public class ConfigurationTree {

  // -- Fields --

  /** Directory on the file system associated with the tree root. */
  private String rootDir;

  /**
   * Root of tree structure containing configuration data.
   * Each node's user object is a hashtable of key/value pairs.
   */
  private DefaultMutableTreeNode root;

  /** Current file within the tree structure. */
  private String currentId;

  /** Current position within the tree structure. */
  private DefaultMutableTreeNode pos;

  /** Series-independent hashtable for current file. */
  private Hashtable globalTable;

  /** Series-specific hashtable for current file's active series. */
  private Hashtable seriesTable;

  // -- Constructor --

  /**
   * Creates a new configuration tree rooted at
   * the given directory in the file system.
   */
  public ConfigurationTree(String rootDir) {
    this.rootDir = new File(rootDir).getAbsolutePath();
    root = new DefaultMutableTreeNode();
  }

  // -- ConfigurationTree API methods --

  /** Parses the given Bio-Formats configuration file. */
  public void parseConfigFile(String configFile) throws IOException {
    File file = new File(configFile);
    configFile = file.getAbsolutePath();
    String dir = file.getParentFile().getAbsolutePath();

    int count = 0;
    BufferedReader in = new BufferedReader(new FileReader(configFile));
    while (true) {
      String line = in.readLine();
      if (line == null) break;
      line = line.trim();
      count++;
      if (line.startsWith("#")) continue; // ignore comments

      // parse filename
      int start = 0;
      int end = -1;
      if (line.startsWith("\"")) {
        start = 1;
        end = line.indexOf("\"", 1);
      }
      if (end < 0) end = line.indexOf(" ");
      if (end < 0) end = line.length();
      String id = line.substring(start, end);
      id = new File(dir, id).getAbsolutePath();

      DefaultMutableTreeNode node = findNode(id, true);
      if (node == null) {
        LogTools.println("Warning: config file '" +
          configFile + "' has invalid filename on line " + count);
        continue;
      }
      Hashtable global = (Hashtable) node.getUserObject();

      Hashtable local = null;

      StringTokenizer st = new StringTokenizer(line.substring(end + 1));
      while (st.hasMoreTokens()) {
        String token = st.nextToken();

        boolean left = token.startsWith("[");
        boolean right = token.endsWith("]");
        if (left && right) token = token.substring(1, token.length() - 1);
        else if (left) token = token.substring(1);
        else if (right) token = token.substring(0, token.length() - 1);

        if (left) {
          // begin series context
          if (local != null) {
            LogTools.println("Warning: config file '" +
              configFile + "' has unmatched [ on line " + count);
          }
          local = new Hashtable();
        }

        int equals = token.indexOf("=");
        if (token.equals("")) { } // ignore blank tokens
        else if (equals < 0) {
          // ignore invalid tokens
          LogTools.println("Warning: config file '" + configFile +
            "' has invalid token on line " + count + ": " + token);
        }
        else {
          // store key/value pair into the proper context
          String key = token.substring(0, equals);
          String value = token.substring(equals + 1);
          if (local == null) global.put(key, value);
          else local.put(key, value);
        }

        if (right) {
          // end series context
          if (local == null) {
            LogTools.println("Warning: config file '" +
              configFile + "' has unmatched ] on line " + count);
          }
          // save local context results
          int index = toInt((String) local.get("series"));
          if (index < 0) {
            LogTools.println("Warning: config file '" + configFile +
              "' has invalid series block on line " + count);
          }
          else getChild(node, index).setUserObject(local);
          local = null;
        }
      }
    }
    in.close();
  }

  /** Sets the current file. */
  public void setId(String id) {
    if (id.equals(currentId)) return;
    currentId = id;
    pos = findNode(id, false);
    if (pos == null) globalTable = null;
    else globalTable = (Hashtable) pos.getUserObject();
    seriesTable = null;
  }

  /** Sets the active series. */
  public void setSeries(int series) {
    if (pos == null) return; // file has no configuration
    if (series >= pos.getChildCount()) {
      LogTools.println("Warning: invalid series for file '" +
        currentId + "': " + series);
      seriesTable = null;
      return;
    }
    DefaultMutableTreeNode child =
      (DefaultMutableTreeNode) pos.getChildAt(series);
    seriesTable = (Hashtable) child.getUserObject();
  }

  // - Convenience methods for accessing specific key values -

  public int getNumSeries() { return toInt(getValue("total_series")); }

  public int getX() { return toInt(getSeriesValue("x")); }
  public int getY() { return toInt(getSeriesValue("y")); }
  public int getZ() { return toInt(getSeriesValue("z")); }
  public int getC() { return toInt(getSeriesValue("c")); }
  public int getT() { return toInt(getSeriesValue("t")); }

  public String getOrder() { return getSeriesValue("order"); }

  public boolean isInterleaved() {
    return toBoolean(getSeriesValue("interleave"));
  }

  public boolean isRGB() {
    return toBoolean(getSeriesValue("rgb"));
  }

  public int getThumbX() {
    return toInt(getSeriesValue("thumbx"));
  }

  public int getThumbY() {
    return toInt(getSeriesValue("thumby"));
  }

  public int getPixelType() {
    String type = getSeriesValue("type");
    return type == null ? -1 : FormatTools.pixelTypeFromString(type);
  }

  public boolean isLittleEndian() {
    return toBoolean(getSeriesValue("little"));
  }

  public String getMD5() { return getSeriesValue("md5"); }

  public float getTimePerPlane() {
    return toFloat(getValue("access"));
  }

  public int getMemoryUse() {
    return toInt(getValue("mem"));
  }

  public boolean isTestable() {
    return toBoolean(getValue("test"));
  }

  // -- Helper methods --

  /** Gets the tree node associated with the given file. */
  private DefaultMutableTreeNode findNode(String id, boolean create) {
    if (!id.startsWith(rootDir)) return null;
    id = id.substring(rootDir.length());
    StringTokenizer st = new StringTokenizer(id, "\\/");
    DefaultMutableTreeNode node = root;
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      Enumeration en = node.children();
      DefaultMutableTreeNode next = null;
      while (en.hasMoreElements()) {
        DefaultMutableTreeNode child =
          (DefaultMutableTreeNode) en.nextElement();
        Hashtable table = (Hashtable) child.getUserObject();
        if (token.equals(table.get("name"))) {
          next = child;
          break;
        }
      }
      if (next == null) {
        // create node, if applicable
        if (!create) return null;
        next = new DefaultMutableTreeNode();
        Hashtable table = new Hashtable();
        next.setUserObject(table);
        table.put("name", token);
        node.add(next);
      }
      node = next;
    }
    return node;
  }

  /** Gets a value associated with the current file. */
  private String getValue(String key) {
    return globalTable == null ? null : (String) globalTable.get(key);
  }

  /** Gets a value associated with the current file and active series. */
  private String getSeriesValue(String key) {
    return seriesTable == null ? null : (String) seriesTable.get(key);
  }

  /** Converts a String to a boolean. */
  private boolean toBoolean(String s) {
    return s == null || !s.equals("false");
  }

  /** Converts a String to a float. */
  private float toFloat(String s) {
    float result = Float.NaN;
    if (s != null) {
      try {
        result = Float.parseFloat(s);
      }
      catch (NumberFormatException exc) { }
    }
    return result;
  }

  /** Converts a String to an int. */
  private int toInt(String s) {
    int result = 0;
    if (s != null) {
      try {
        result = Integer.parseInt(s);
      }
      catch (NumberFormatException exc) { }
    }
    return result;
  }

  // -- Utility methods --

  /**
   * Gets the child at the specified index in the given node's child array,
   * creating child nodes if necessary.
   */
  private static DefaultMutableTreeNode getChild(DefaultMutableTreeNode node,
    int index)
  {
    for (int i=node.getChildCount(); i<=index; i++) {
      node.add(new DefaultMutableTreeNode());
    }
    return (DefaultMutableTreeNode) node.getChildAt(index);
  }

}
