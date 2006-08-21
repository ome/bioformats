//
// RedBlackTree.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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

package loci.formats.in;

import java.util.*;

/**
 * An OLE parser helper class for building a filesystem as a red-black tree.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class RedBlackTree {

  // -- Constants --

  private static final int ROOT = 5, FILE = 2;

  // -- Fields --

  /** The root of the tree. */
  private RedBlackTreeNode root;

  /** Number of leaf nodes. */
  private int numLeaves;

  /**
   * Add a node to the tree.  Note that we do NOT explicitly check that the
   * node to be added conforms to the rules for red-black tree construction.
   * This is because most of our OLE-variant files don't contain properly
   * colored trees (i.e. the root node is red, and the remaining nodes are
   * black, so it doesn't work for either assignment of colors).
   *
   * @param color the node's color
   * @param type the node's type (root, file or directory)
   * @param block the block number of this node (index in property table array)
   * @param next the block number of the next sibling node
   * @param parent the block number of the parent node
   */
  public void add(int color, int type, int block, int next, int parent,
    String name, int firstDataBlock, int fileSize)
  {
    // first construct a new "dummy" node
    RedBlackTreeNode newNode = new RedBlackTreeNode(color, type, block, name,
      firstDataBlock, fileSize);

    if (type == ROOT) {
      // add this node as the root of the tree
      newNode.isLast = true;
      newNode.depth = 0;
      root = newNode;
      return;
    }

    if (next == -1) newNode.isLast = true;

    // search for the parent

    RedBlackTreeNode parentNode = bfs(parent);

    // stupid way of constructing the path name
    name = parentNode.getName() + "/ " + name;
    newNode.setName(name);

    // set the parent

    newNode.depth = parentNode.depth + 1;
    newNode.parent = parentNode;
    parentNode.children.add(newNode);
    if (type == FILE) numLeaves++;
  }

  /** Get a list of all leaf nodes (files). */
  public Vector getLeaf() {
    Vector leaves = new Vector();

    // we expect numLeaves leaf nodes

    int numFound = 0;

    Vector childs = new Vector();
    childs.add(root);
    while ((numFound < numLeaves) && (childs.size() > 0)) {
      RedBlackTreeNode firstChild = (RedBlackTreeNode) childs.get(0);
      childs.remove(0);
      if (firstChild.getType() == FILE) leaves.add(firstChild);
      else {
        for (int i=0; i<firstChild.children.size(); i++) {
          childs.add(firstChild.children.get(i));
        }
      }
    }

    return leaves;
  }

  /** Breadth-first search. */
  private RedBlackTreeNode bfs(int block) {
    Vector childs = new Vector();
    childs.add(root);

    while (childs.size() > 0) {
      RedBlackTreeNode firstChild = (RedBlackTreeNode) childs.get(0);
      childs.remove(0);
      if (firstChild.getBlock() == block) return firstChild;
      for (int i=0; i<firstChild.children.size(); i++) {
        childs.add(firstChild.children.get(i));
      }
    }
    return null;
  }

  /** Return true if the root node is null. */
  public boolean nullRoot() {
    return root == null;
  }

}
