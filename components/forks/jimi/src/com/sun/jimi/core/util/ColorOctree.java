package com.sun.jimi.core.util;

import java.awt.Color;
import java.awt.image.ImageProducer;

import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.util.ExpandableArray;

/**
 * This class implements a color reduction octree algorithm
 * as discussed in the following article.
 * "A simple method for color quantization: Octree quantization"
 * by Michael Gervautz, Werner Purgathofer in book "Graphics Gems"
 *
 * Once the number of color representations exceeds the maximum
 * [generally max will be 256] then a pair of Octree nodes which
 * each represent a color and have the minimum color difference
 * between them will be located and merged. For minimum color
 * distance these nodes will both be at deepest level of the
 * octree tree.
 *
 * @author	Robin Luiten
 * @version	1.0	11/Sep/1997
 */
public final class ColorOctree implements OctreeCallback
{
	/** The level at which leaves are being added currently */
	int leaf_level;

	/** The maximum number of colors to build the octree for */
	int maxColors;

	/** root node of octree */
	OctreeNode tree;

	/**
	 * List of reducible nodes at each level of Octree.
	 * One list for each depth level of Octree.
	 * Number of depth levels which may be reducible are 
	 * between 1 and leaf_level - 1.
	 */
	ExpandableArray[] reduce;

	/**
	 * Record of the number of leaf nodes in this octree
	 */
	int numLeaves;

	/**
	 * Set to true if alpha-data is being used.
	 * If so, the last index in the color map will be reserved for
	 * a transparent color.
	 */
	protected boolean alpha;

	public ColorOctree(int maxColors)
	{
		int i;

		this.numLeaves = 0;
		this.leaf_level = OctreeCallback.MAXLEV;
		this.maxColors = maxColors;
		this.tree = new OctreeNode(this);	// top level

		// Initialisation for OctreeCallback data structure
		reduce = new ExpandableArray[OctreeCallback.MAXLEV];	// 0 to 7.
		for (i = 0; i < OctreeCallback.MAXLEV; ++i)
			reduce[i] = new ExpandableArray(10, 10);

		cachedONodes = new ExpandableArray(10);
	}

	public void addColor(int color)
	{
		// transparent?
		if ((color & 0xFF000000) == 0) {
			// if alpha isn't catered for yet
			if (!alpha) {
				alpha = true;
				maxColors--;
				if (numLeaves > maxColors) {
					reduceColors();
				}
			}
			// ignore - have all the alpha information required
		}
		else {
			numLeaves += tree.insertColor(color, leaf_level);

			// numLeaves can only be 1 greater than maxColors here.
			if (numLeaves > maxColors)
				reduceColors();
		}
	}

	public void addColor(int[] color)
	{
		int i;

		for (i = 0; i < color.length; ++i)
		{
			// transparent?
			if ((color[i] & 0xFF000000) == 0) {
				// if alpha isn't catered for yet
				if (!alpha) {
					alpha = true;
					maxColors--;
					if (numLeaves > maxColors) {
						reduceColors();
					}
				}
				// ignore - have all the alpha information required
			}
			else {
				numLeaves += tree.insertColor(color[i], leaf_level);

				// numLeaves can only be 1 greater than maxColors here.
				if (numLeaves > maxColors)
					reduceColors();
			}
		}
	}

	/**
	 * Returns true if the last color in the palette has been reserved for
	 * a transparent color.
	 */
	public boolean hasAlpha()
	{
		return alpha;
	}

	void reduceColors()
	{
		OctreeNode on;
		caching = true;	// disable the caching for now.... fiX

		on = getReducible();
		//OctreeNode.dumpLevCounts();

		numLeaves -= on.collapseOctree();
		//P.rt("## Node at level " + on.level + " num leaves left: " + numLeaves);

		// reducible node is now a leaf
		on.leaf = true;
		++numLeaves;

		// reduce leaf_level if the most recent found reducible node
		// is more than one step away from the leaf level.
		if (on.level < (leaf_level - 1))
			leaf_level = on.level + 1;
	}


	/**
	 * Retrieve the pallete as built up in the color reduction octree
	 * @param p array of bytes to write palette data to. Must be large
	 * enough to gold all the colors required. [256 * 3 is always large
	 * enough]
	 * @return the number of colors written in packed byte RGB representation
	 * into the pallete array. The data written to the color palette array
	 * takes space up to the return value * 3.
	 */
	int getPalette(byte[] p)
	{
		// 256 colors, 4 channels
		byte[] buf = new byte[256 * 4];
		int size = tree.createPalette(buf, 0) / 3;
		size = Math.min(size, maxColors);
		// add alphas (opaque)
		int bufidx = 0;
		int pidx = 0;
		while (bufidx < size * 3) {
			p[pidx++] = buf[bufidx++];
			p[pidx++] = buf[bufidx++];
			p[pidx++] = buf[bufidx++];
			p[pidx++] = (byte)0xff;
		}
		return alpha ? size + 1 : size;
	}

	/**
	 * This method() must be called after a call to getPalette() because
	 * otherwise all palette index's returned here will be -1.
	 * @param color the color to get the quantized palette entry for.
	 * @return the index into the color palette which represents
	 * the parameter color in the reduced palette.
	 */
	int quantizeColor(int color)
	{
		// transparent?
		if ((color & 0xff000000) == 0) {
			return maxColors * 3;
		}
		else {
			return tree.quantizeColor(color);
		}
	}

	/**
	 * Part of OctreeCallback interface.
	 */
	public void markReducible(OctreeNode on)
	{
		on.marked = true;	// mark as being reducible and recorded as such
		reduce[on.level].addElement(on);
	}

	/**
	 * Part of OctreeCallback interface.
	 *
	 * This implementation searches for the depeest registered reducible
	 * node with the most number of pixels recorded for it.
	 *
J	 * This would benefit from an ordered data structure.. [ fiX LATER ]
	 * @return OctreeNode which is the "best" candidate for reducing current
	 * number of leaf nodes in the Octree back down to max colors allowed.
	 */
	public OctreeNode getReducible()
	{
		int i;
		int j;
		ExpandableArray v;
		int vSize;
		OctreeNode on;
		OctreeNode onLargest = null; // reference to largest octreenode

		//P.rt("reduce[].size() = "   +reduce[0].size()+ " " +reduce[1].size()+" " +reduce[2].size()+ " " +reduce[3].size()+ " " +reduce[4].size()+ " " +reduce[5].size()+ " " +reduce[6].size()+ " " +reduce[7].size());
		// Reducible nodes are always above leaf_level or higher
		for (i = leaf_level - 1; reduce[i].size() == 0; --i)
			/* null body */;

        int largestIndex;
		v = reduce[i];
		vSize = v.size();
		if (vSize > 0)
		{
		    largestIndex = 0;
			onLargest = (OctreeNode)v.elementAt(0);
			for (j = 1; j < vSize; ++j)
			{
				on = (OctreeNode)v.elementAt(j);
				if (on.count >= onLargest.count)
				{
					onLargest = on;
					largestIndex = j;
			    }
			}
			v.removeElementAt(largestIndex);
		}

		return onLargest;
	}

	/**
	 * Return an appropriately initialised OctreeNode object.
	 * It attempts to return a cached node if theres one available
	 * if none available it creates a new node.
	 */
	public OctreeNode getONode(OctreeCallback oc, int level)
	{
		OctreeNode on;

		if (cacheCount > 0)
		{
			on = (OctreeNode)cachedONodes.lastElement();
			cachedONodes.removeElementAt(cacheCount - 1);
			--cacheCount;
			on.setFields(oc, level);
		}
		else
		{
			on = new OctreeNode(oc, level);
		}
		return on;
	}


	/** secondary copy of count in cachedONodes for quick access */
	int cacheCount;

	/** cache of ONodex */
	public ExpandableArray cachedONodes;

	/** no point in caching more nodes than this. */
	public final static int MAXCACHE = 25;

	/** flag to disable OctreeNode object caching */
	boolean caching;

	/**
	 * Called with an OctreeNode that is no longer required
	 * this method() caches the node if there are insufficient
	 * nodes cached. 
	 */
	public void cacheONode(OctreeNode on)
	{
		--OctreeNode.levCounts[on.level];

		if (caching)
			if (cacheCount < MAXCACHE)
			{
				cachedONodes.addElement(on);
				++cacheCount;
			}
	}
}


