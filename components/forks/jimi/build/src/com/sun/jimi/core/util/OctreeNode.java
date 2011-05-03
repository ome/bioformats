package com.sun.jimi.core.util;
import java.awt.Color;

/**
 * This class is the core class of the Octree Color reduction
 * system.<p>
 * This class represents colours as a path from root of Octree to Leaf.
 * This octree has been implemented for encoding of 24 bit colors made 
 * up of 8 bit components of red/green/blue.
 * The colour components are encoded from Most Significant bit
 * down to least significant bit for each of the Red/Green/Blue components.<p>
 *
 * Each node contains the sum of all Red/Green/Blue color components
 * of all nodes and colors added to the octree below this node.
 * Therefore the average colour for all those components can be
 * calculated at anytime by dividing by count.<p>
 *
 * This class calls several procedures in the OctreeCallback class
 * for the purposes of creating OctreeNode objects, releasing 
 * of OctreeNode objects, marking of OctreeNode objects for later
 * reduction.<p>
 *
 * Possible future optimisation might be to remember last color added 
 * and if next is same color then just bump count and details for the node.<p>
 *
 * The following data redundancy currently exists internally childCount == 0 is same as leaf == true.<p>
 *
 * @author	Robin Luiten
 * @version	1.0	15/Sep/1997
 */
public class OctreeNode
{
	/** debug trace flag */
	static boolean debug;

	/** debug count for the number of nodes allocated at each level */
	static int levCounts[];

	static
	{
		int i;
		levCounts = new int[OctreeCallback.MAXLEV + 1];
		for (i = 0; i < OctreeCallback.MAXLEV + 1; ++i)
			levCounts[i] = 0;
	}

	static void dumpLevCounts()
	{
		int i;
		String str = "";
		for (i = 0; i < OctreeCallback.MAXLEV + 1; ++i)
			str += " " + levCounts[i] + " ";
		System.out.println("levCounts " + str);
	}

	/** debug trace count of these nodes allocated */
	public static int numNodes = 0;

	/** This is a leaf node */
	boolean leaf;

	/** flag indicating this node is reducible and recorded as such */
	boolean marked;

	/** 
	 * Sum of the colors components merged to this node and represented
	 * by this node and all nodes below it in the octree.
	 * This is a "long" variable because as an integer it would only be
	 * able to hold the sum of 2^24 pixels without overflow.
	 */
	long sumR;
	long sumG;
	long sumB;

	/**
	 * Count of all color entries stored at or below this octree node.
	 */
	int count;
	
	/**
	 * Octree branches for each dimension.
	 * This is not an array mainly because arrays are slow in java.
	 */
	OctreeNode r0g0b0;
	OctreeNode r0g0b1;
	OctreeNode r0g1b0;
	OctreeNode r0g1b1;
	OctreeNode r1g0b0;
	OctreeNode r1g0b1;
	OctreeNode r1g1b0;
	OctreeNode r1g1b1;

	/** count of number of child nodes below this node */
	int childCount;

	/**
	 * Level of this node in the octree.
	 * Note Level 0 is the root of the tree.
	 */
	int level;

	/** 
	 * Mask to use to extract the color bit for this current
	 * level to decide which child branch encodes this color.
	 * NOTE: colors are handled as integers.
	 */
	int redMask;
	int greenMask;
	int blueMask;

	/**
	 * index into pallete for where the color represented by this
	 * node is stored.
	 */
	int pIndex;

	/**
	 * Callback handle for marking and retrieving reducible octree nodes.
	 */
	OctreeCallback oc;

	public String toString()
	{
		String str;
		str = "l " + leaf;
		str += " m " + marked;
		str += " count " + count;
		str += " childCount " + childCount;
		str += " level " + level;
		if (r0g0b0 != null)
			str += " r0g0b0 ";
		if (r0g0b1 != null)
			str += " r0g0b1 ";
		if (r0g1b0 != null)
			str += " r0g1b0 ";
		if (r0g1b1 != null)
			str += " r0g1b1 ";
		if (r1g0b0 != null)
			str += " r1g0b0 ";
		if (r1g0b1 != null)
			str += " r1g0b1 ";
		if (r1g1b0 != null)
			str += " r1g1b0 ";
		if (r1g1b1 != null)
			str += " r1g1b1 ";
		return str;
	}

	/**
	 * Debug method to dump entire octree in a useful trace form
	 * Simply call on root node    root.dump("")
	 */
	public void dump(String pre)
	{
		System.out.println(pre + "[" + level + "]" + toString());
		if (r1g1b1 != null)
			r1g1b1.dump(pre + "111 ");
		if (r1g1b0 != null)
			r1g1b0.dump(pre + "110 ");
		if (r1g0b1 != null)
			r1g0b1.dump(pre + "101 ");
		if (r1g0b0 != null)
			r1g0b0.dump(pre + "100 ");
		if (r0g1b1 != null)
			r0g1b1.dump(pre + "011 ");
		if (r0g1b0 != null)
			r0g1b0.dump(pre + "010 ");
		if (r0g0b1 != null)
			r0g0b1.dump(pre + "001 ");
		if (r0g0b0 != null)
			r0g0b0.dump(pre + "000 ");
	}

	/**
	 * @param co the color octree class using this node in its octree
	 * structure. Used to allow callback to markReducible() and getReducible()
	 * procedures.
	 */
	OctreeNode(OctreeCallback oc)
	{
		this(oc, 0);				// default to creating a root node
	}

	/**
	 * @param co the color octree class using this node in its octree
	 * structure. Used to allow callback to markReducible() and getReducible()
	 * procedures.
	 * @param level the level at which this OctreeNode will be put in the tree.
	 */
	OctreeNode(OctreeCallback oc, int level)
	{
		++numNodes;
		setFields(oc, level);
	}

	/**
	 * This is the basic constructor function.
	 * Placed here to allow a higher level cache mechanism for the
	 * OctreeNodes to reuse octree nodes.
	 */
	void setFields(OctreeCallback oc, int level)
	{
		this.count = 0;
		this.sumR = 0;
		this.sumG = 0;
		this.sumB = 0;
		this.level = level;
		this.pIndex = -1;
		this.childCount = 0;
		this.leaf = false;
		this.marked = false;

		r0g0b0 = r0g0b1 = r0g1b0 = r0g1b1 =
		r1g0b0 = r1g0b1 = r1g1b0 = r1g1b1 = null;

		// masks have no meaning at level 8
		// at level 0 the most significant colour bit of each channel is encoded.
		// at level 7 the least significant colour bit of each channel is encoded
		if (level != ColorOctree.MAXLEV)
		{
			blueMask  = 0x80 >> level;
			greenMask = blueMask << 8;
			redMask   = greenMask << 8;
		}

		this.oc = oc;

		// debug
		++levCounts[level];
	}

	/**
	 * @color the color to find the octree color representation for.
	 * @return the node which matches the color path through the octree 
	 * for the given color as deep as can be found in the octree.
	 */
	OctreeNode findChild(int color)
	{

		if (childCount == 0)		// small optimisation
		{
			return this;
		}

		// extract colour bit to decide which color branch for this color
		int rb = (color & redMask);
		int gb = (color & greenMask);
		int bb = (color & blueMask);

		if (rb == 0)
		{
			if (gb == 0)
				if (bb == 0)
					if (r0g0b0 != null)
					{
						return r0g0b0.findChild(color);
					}
					else
					{
						return this;
					}
				else // blue bit 1
					if (r0g0b1 != null)
					{
						return r0g0b1.findChild(color);
					}
					else
					{
						return this;
					}
			else // green bit 1
				if (bb == 0)
					if (r0g1b0 != null)
						return r0g1b0.findChild(color);
					else
						return this;
				else // blue bit 1
					if (r0g1b1 != null)
						return r0g1b1.findChild(color);
					else
						return this;
		}
		else // red bit 1
		{
			if (gb == 0)
				if (bb == 0)
					if (r1g0b0 != null)
						return r1g0b0.findChild(color);
					else
						return this;
				else // blue bit 1
					if (r1g0b1 != null)
						return r1g0b1.findChild(color);
					else
						return this;
			else // green bit 1
				if (bb == 0)
					if (r1g1b0 != null)
						return r1g1b0.findChild(color);
					else
						return this;
				else // blue bit 1
					if (r1g1b1 != null)
						return r1g1b1.findChild(color);
					else
						return this;
		}
	}

	/**
	 * This method adds a new color value to the octree, building the 
	 * octree path nodes & links to encode the color as required.
	 *
	 * @param color the color to be added to octree.
	 * @leaf_level maximum level at which leaves are inserted.
	 * @return the number of leafnodes added by this insertColor call.
	 * This will always be Zero or One.
	 */
	int insertColor(int color, int leaf_level)
	{
		int leafCount = 0;
		int nextLev = this.level + 1;

		++count;
		sumR += (color & 0xFF0000) >> 16;
		sumG += (color & 0x00FF00) >> 8;
		sumB += (color & 0x0000FF);

		// if not at a leaf, and less than leaf level recurse.
		if (!leaf) // && (level < leaf_level))
		{
			// extract colour bit to decide which color branch for this color
			int rb = (color & redMask);
			int gb = (color & greenMask);
			int bb = (color & blueMask);
			if (rb == 0)
			{
				if (gb == 0)
				{
					if (bb == 0)
					{
						if (r0g0b0 == null)
						{
							r0g0b0 = oc.getONode(oc, nextLev);
							if (nextLev >= leaf_level)
							{
								r0g0b0.leaf = true;
								++leafCount;
							}
							++childCount;
						}
						leafCount += r0g0b0.insertColor(color, leaf_level);
					}
					else // blue bit 1
					{
						if (r0g0b1 == null)
						{
							r0g0b1 = oc.getONode(oc, nextLev);
							if (nextLev >= leaf_level)
							{
								r0g0b1.leaf = true;
								++leafCount;
							}
							++childCount;
						}
						leafCount += r0g0b1.insertColor(color, leaf_level);
					}
				}
				else // green bit 1
				{
					if (bb == 0)
					{
						if (r0g1b0 == null)
						{
							r0g1b0 = oc.getONode(oc, nextLev);
							if (nextLev >= leaf_level)
							{
								r0g1b0.leaf = true;
								++leafCount;
							}
							++childCount;
						}
						leafCount += r0g1b0.insertColor(color, leaf_level);
					}
					else // blue bit 1
					{
						if (r0g1b1 == null)
						{
							r0g1b1 = oc.getONode(oc, nextLev);
							if (nextLev >= leaf_level)
							{
								r0g1b1.leaf = true;
								++leafCount;
							}
							++childCount;
						}
						leafCount += r0g1b1.insertColor(color, leaf_level);
					}
				}
			}
			else // red bit 1
			{
				if (gb == 0)
				{
					if (bb == 0)
					{
						if (r1g0b0 == null)
						{
							r1g0b0 = oc.getONode(oc, nextLev);
							if (nextLev >= leaf_level)
							{
								r1g0b0.leaf = true;
								++leafCount;
							}
							++childCount;
						}
						leafCount += r1g0b0.insertColor(color, leaf_level);
					}
					else // blue bit 1
					{
						if (r1g0b1 == null)
						{
							r1g0b1 = oc.getONode(oc, nextLev);
							if (nextLev >= leaf_level)
							{
								r1g0b1.leaf = true;
								++leafCount;
							}
							++childCount;
						}
						leafCount += r1g0b1.insertColor(color, leaf_level);
					}
				}
				else // green bit 1
				{
					if (bb == 0)
					{
						if (r1g1b0 == null)
						{
							r1g1b0 = oc.getONode(oc, nextLev);
							if (nextLev >= leaf_level)
							{
								r1g1b0.leaf = true;
								++leafCount;
							}
							++childCount;
						}
						leafCount += r1g1b0.insertColor(color, leaf_level);
					}
					else // blue bit 1
					{
						if (r1g1b1 == null)
						{
							r1g1b1 = oc.getONode(oc, nextLev);
							if (nextLev >= leaf_level)
							{
								r1g1b1.leaf = true;
								++leafCount;
							}
							++childCount;
						}
						leafCount += r1g1b1.insertColor(color, leaf_level);
					}
				}
			}
			if (childCount > 1 && !marked)
				oc.markReducible(this);	// mark this node as reducible
		}

		return leafCount;
	}

	/**
	 * This method removes all the child nodes under the current node from
	 * the octree structure. The OctreeCallback method cacheONode() is called
	 * for each node released in this way.
	 * @return the number of leaf nodes which have been collapsed below
	 * the node that this method is called on.
	 */
	int collapseOctree()
	{
		int leavesCollapsed = 0;

		if (childCount == 0)		// no more entries below this node
			return 0;
		
		if (r0g0b0 != null)
		{
			if (r0g0b0.leaf)
				++leavesCollapsed;
			leavesCollapsed += r0g0b0.collapseOctree();
			oc.cacheONode(r0g0b0);
		}
		if (r0g0b1 != null)
		{
			if (r0g0b1.leaf)
				++leavesCollapsed;
			leavesCollapsed += r0g0b1.collapseOctree();
			oc.cacheONode(r0g0b1);
		}
		if (r0g1b0 != null)
		{
			if (r0g1b0.leaf)
				++leavesCollapsed;
			leavesCollapsed += r0g1b0.collapseOctree();
			oc.cacheONode(r0g1b0);
		}
		if (r0g1b1 != null)
		{
			if (r0g1b1.leaf)
				++leavesCollapsed;
			leavesCollapsed += r0g1b1.collapseOctree();
			oc.cacheONode(r0g1b1);
		}

		if (r1g0b0 != null)
		{
			if (r1g0b0.leaf)
				++leavesCollapsed;
			leavesCollapsed += r1g0b0.collapseOctree();
			oc.cacheONode(r1g0b0);
		}
		if (r1g0b1 != null)
		{
			if (r1g0b1.leaf)
				++leavesCollapsed;
			leavesCollapsed += r1g0b1.collapseOctree();
			oc.cacheONode(r1g0b1);
		}
		if (r1g1b0 != null)
		{
			if (r1g1b0.leaf)
				++leavesCollapsed;
			leavesCollapsed += r1g1b0.collapseOctree();
			oc.cacheONode(r1g1b0);
		}
		if (r1g1b1 != null)
		{
			if (r1g1b1.leaf)
				++leavesCollapsed;
			leavesCollapsed += r1g1b1.collapseOctree();
			oc.cacheONode(r1g1b1);
		}

		r0g0b0 = null;
		r0g0b1 = null;
		r0g1b0 = null;
		r0g1b1 = null;
		r1g0b0 = null;
		r1g0b1 = null;
		r1g1b0 = null;
		r1g1b1 = null;
		childCount = 0;
		return leavesCollapsed;
	}

	/**
	 * @return the color represented by the collected colors
	 * of and below the current node in the octree.
	 */
	final int getColor()
	{
		int r = (int)(sumR / count);
		int g = (int)(sumG / count);
		int b = (int)(sumB / count);
		return (r << 16) + (g << 8) + (b);
	}

	/**
	 * This method searchs for leaf nodes in the octree and enters
	 * them into the appropriatte palette locations.
	 * @param p Array to place the pallete entries into.
	 * Array must be large enough to hold all colors in the octree.
	 * The colors are stored in the array in a packed color component
	 * format in the order Red/Gree/Blue.
	 * @param base the index to where in the array to place the next found
	 * colors.
	 * @return how many palette entries were found in this level * 3.
	 * [times 3 because outputing RGB in bytes]
	 */
	int createPalette(byte[] p, int base)
	{
		int entryCount = 0;

		// I dont agree with level == leaf_level - it doesnt twig.
		if (leaf) // || level = leaf_level)
		{
			p[base    ] = (byte)(sumR / count);
			p[base + 1] = (byte)(sumG / count);
			p[base + 2] = (byte)(sumB / count);
			pIndex = base;		// return index into colorMap array for this palette entry
			entryCount += 3;	// 3 bytes for each colour
		}

		if (childCount > 0)
		{
			if (r0g0b0 != null)
				entryCount += r0g0b0.createPalette(p, base + entryCount);
			if (r0g0b1 != null)
				entryCount += r0g0b1.createPalette(p, base + entryCount);
			if (r0g1b0 != null)
				entryCount += r0g1b0.createPalette(p, base + entryCount);
			if (r0g1b1 != null)
				entryCount += r0g1b1.createPalette(p, base + entryCount);
			if (r1g0b0 != null)
				entryCount += r1g0b0.createPalette(p, base + entryCount);
			if (r1g0b1 != null)
				entryCount += r1g0b1.createPalette(p, base + entryCount);
			if (r1g1b0 != null)
				entryCount += r1g1b0.createPalette(p, base + entryCount);
			if (r1g1b1 != null)
				entryCount += r1g1b1.createPalette(p, base + entryCount);
		}
		return entryCount;
	}

	/**
	 * @color the color from the original image to be quantised via lookup
	 * in the pallete generated from the reduced octree.
	 * @return the index into the palette for the quantized color. This index
	 * is the index into the byte[] colormap array so is actually 3 times the
	 * real color index into a straight palette table of colors.
	 */
	final int quantizeColor(int c)
	{
		if (leaf)
			return pIndex;
		else
		{
			OctreeNode on = findChild(c);
			if (on.leaf == false)
			{
				OctreeNode.debug = true;
				on = findChild(c);
				OctreeNode.debug = false;
				//debug
				if (level == 0)
					dump("");
			}

			return findChild(c).pIndex;
		}
	}

}

