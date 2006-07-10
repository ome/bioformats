//
// RedBlackTreeNode.java
//

package loci.formats.in;

import java.util.Vector;

/**
 * Class representing nodes in a red-black tree.
 */
public class RedBlackTreeNode {

  // -- Fields --

  /** Pointer to the parent node. */
  public RedBlackTreeNode parent;

  /** Vector of pointers to the child nodes. */
  public Vector children;

  /** True if this node is the rightmost in its level. */
  public boolean isLast;

  /** Depth of the node (from the root). */
  public int depth;

  /** Color of the node. */
  private int color;

  /** Node type. */
  private int type;

  /** Block number of this node. */
  private int block;

  /** Name of the node. */
  private String name;

  /** First block of the node's data. */
  private int firstDataBlock;

  /** Size of the file. */
  private int fileSize;

  // -- Constructor --

  public RedBlackTreeNode(int color, int type, int block, String name,
    int firstDataBlock, int fileSize)
  {
    this.color = color;
    this.type = type;
    this.block = block;
    this.name = name;
    this.firstDataBlock = firstDataBlock;
    this.fileSize = fileSize;
    children = new Vector();
  }

  /** Retrieve the node's name. */
  public String getName() { return name; }

  /** Set the node's name. */
  public void setName(String name) { this.name = name; }

  /** Retrieve the node's first data block. */
  public int getFirstBlock() { return firstDataBlock; }

  /** Retrieve block number. */
  public int getBlock() { return block; }

  /** Retrieve the type. */
  public int getType() { return type; }

  /** Retrieve the file size. */
  public int getSize() { return fileSize; }

}
