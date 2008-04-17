//
// LZWTreeNode.java
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

package loci.formats.codec;

/**
 * An LZW-compression helper class for building a symbol table in tree format.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/codec/LZWTreeNode.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/LZWTreeNode.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class LZWTreeNode {

  // -- Fields --

  /** List of up to 256 children. */
  protected LZWTreeNode[] children;

  /** Code corresponding to this node. */
  protected int theCode;

  // -- Constructor --

  /** Constructs a new LZW symbol tree node. */
  public LZWTreeNode(int code) {
    children = new LZWTreeNode[256];
    theCode = code;
  }

  // -- LZWTreeNode API methods --

  /** Initializes this node as the root of the symbol table. */
  public void initialize() {
    for (int i=0; i<256; i++) children[i] = new LZWTreeNode(i);
  }

  /** Gets the code corresponding to this node. */
  public int getCode() {
    return theCode;
  }

  /** Gets this node's indexth child. */
  public LZWTreeNode getChild(byte index) {
    int ndx = index;
    if (ndx < 0) ndx += 256;
    return children[ndx];
  }

  /** Sets this node's indexth child to match the given node. */
  public void addChild(int index, LZWTreeNode node) {
    children[index] = node;
  }

  /** Gets the code for the given byte sequence, or -1 if none. */
  public int codeFromString(ByteVector string) {
    LZWTreeNode node = nodeFromString(string);
    return node == null ? -1 : node.theCode;
  }

  /** Gets the node for the given byte sequence, or null if none. */
  public LZWTreeNode nodeFromString(ByteVector string) {
    byte[] b = string.toByteArray();
    LZWTreeNode node = this;
    for (int i=0; i<b.length; i++) {
      int q = (int) b[i];
      if (q < 0) q += 256;
      node = node.children[q];
      if (node == null) return null;
    }
    return node;
  }

  /** Adds the given code for the specified byte sequence. */
  public void addTableEntry(ByteVector string, int code) {
    byte[] b = string.toByteArray();
    LZWTreeNode node = this;
    for (int i=0; i<b.length-1; i++) {
      int q = b[i];
      if (q < 0) q += 256;
      node = node.children[q];
    }
    int q = b[b.length - 1];
    if (q < 0) q += 256;
    node.addChild(q, new LZWTreeNode(code));
  }

}
