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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.tree.DefaultMutableTreeNode;

import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.formats.FormatTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores configuration data about files in a directory structure.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/test-suite/src/loci/tests/testng/ConfigurationTree.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/test-suite/src/loci/tests/testng/ConfigurationTree.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ConfigurationTree {

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(ConfigurationTree.class);

  // -- Fields --

  /** Directory on the file system associated with the tree root. */
  private String rootDir;

  /**
   * Root of tree structure containing configuration data.
   * Each node's user object is a hashtable of key/value pairs.
   */
  private DefaultMutableTreeNode root;

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

  /** Retrieves the Configuration object corresponding to the given file. */
  public Configuration get(String id) throws IOException {
    DefaultMutableTreeNode pos = findNode(id, false, null);
    if (pos == null) return null;
    Hashtable table = (Hashtable) pos.getUserObject();
    return (Configuration) table.get("configuration");
  }

  public void parseConfigFile(String configFile) throws IOException {
    File file = new File(configFile);
    configFile = file.getAbsolutePath();
    String dir = file.getParentFile().getAbsolutePath();

    IniParser parser = new IniParser();
    parser.setCommentDelimiter(null);
    FileInputStream stream = new FileInputStream(configFile);
    IniList iniList = parser.parseINI(new BufferedReader(
      new InputStreamReader(stream, FormatTools.ENCODING)));
    for (IniTable table : iniList) {
      String id = table.get(IniTable.HEADER_KEY);
      id = id.substring(0, id.lastIndexOf(" "));

      id = new File(file.getParent(), id).getAbsolutePath();

      DefaultMutableTreeNode node = findNode(id, true, configFile);
      if (node == null) {
        LOGGER.warn("config file '{}' has invalid filename '{}'",
          configFile, id);
        continue;
      }
    }
  }

  // -- Helper methods --

  /** Gets the tree node associated with the given file. */
  private DefaultMutableTreeNode findNode(String id, boolean create, String configFile) {
    String baseID = id;
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
        table.put("name", token);
        try {
          table.put("configuration", new Configuration(baseID, configFile));
        }
        catch (IOException e) { }
        next.setUserObject(table);
        node.add(next);
      }
      node = next;
    }
    return node;
  }

}
